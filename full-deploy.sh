#!/bin/bash

set -e  # Exit on error
set -o pipefail  # Catch pipeline errors

echo "🚀 Starting Kubernetes Deployment..."

# Step 1: Load .env File
if [ -f .env ]; then
  echo "🔑 Loading environment variables from .env..."
  export $(grep -v '^#' .env | xargs)
  echo "✅ Environment variables loaded!"
else
  echo "❌ .env file not found! Exiting..."
  exit 1
fi

# Step 2: Switch Docker Context
if docker context ls | grep -q "minikube"; then
  docker context use minikube
  echo "✅ Using Minikube Docker context!"
else
  docker context use default
  echo "⚠️ Minikube context not found, using default."
fi

# Step 3: Function to Check & Build Image if Missing
build_if_missing() {
  IMAGE_NAME=$1
  BUILD_PATH=$2
  BUILD_ARGS=$3

  if [[ -z $(docker images -q -f "reference=$IMAGE_NAME:latest") ]]; then
    echo "🛠️ Image $IMAGE_NAME not found. Building..."
    docker build --no-cache -t $IMAGE_NAME:latest $BUILD_ARGS $BUILD_PATH
    echo "✅ Image $IMAGE_NAME built!"
  else
    echo "✅ Image $IMAGE_NAME already exists. Skipping build."
  fi
}

# Step 4: Check & Build Images If Needed
build_if_missing game-price-bot-game ./gpb-game "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-backend ./gpb-backend "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-email ./gpb-email "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-telegram ./gpb-telegram "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-react ./gpb-front "--build-arg BACKEND_SERVICE_URL=$BACKEND_SERVICE_URL --build-arg TELEGRAM_BOT_URL=$TELEGRAM_BOT_URL --build-arg SUPPORT_EMAIL=$SUPPORT_EMAIL"

# Step 5: Load Images into Minikube Cache
if minikube status > /dev/null 2>&1; then
  echo "📤 Loading images into Minikube cache..."
  for image in game-price-bot-backend game-price-bot-game game-price-bot-email game-price-bot-telegram game-price-bot-react; do
    minikube image load $image:latest
  done
  echo "✅ Images loaded into Minikube cache!"
else
  echo "❌ Minikube is not running! Skipping image load..."
fi

# Step 6: Load Secrets into Kubernetes
kubectl delete secret game-price-bot-secret --ignore-not-found
kubectl create secret generic game-price-bot-secret --from-env-file=.env

echo "✅ Secrets created!"

# Step 7: Deploy Kubernetes Services
kubectl apply -f k8s/postgres/postgres.yaml
kubectl apply -f k8s/kafka/
kubectl apply -f k8s/game/
kubectl apply -f k8s/backend/
kubectl apply -f k8s/telegram/
kubectl apply -f k8s/email/
kubectl apply -f k8s/frontend/

echo "✅ All services deployed!"

# Step 8: Restart Deployments Without Removing Pods
declare -A DEPLOYMENT_NAMES=(
    ["game"]="game-service"
    ["backend"]="backend-service"
    ["telegram"]="telegram-bot"
    ["email"]="email-service"
    ["frontend"]="frontend-service"
)

for dep in "${!DEPLOYMENT_NAMES[@]}"; do
  deployment_name="${DEPLOYMENT_NAMES[$dep]}"
  
  echo "🔄 Restarting $deployment_name..."
  kubectl rollout restart deployment/$deployment_name
  kubectl rollout status deployment/$deployment_name
done


# Step 9: Enable Minikube Ingress
if ! minikube addons list | grep -q "ingress.*enabled"; then
  echo "🔄 Enabling Minikube Ingress..."
  minikube addons enable ingress
  minikube addons enable ingress-dns
  echo "✅ Minikube Ingress enabled!"
else
  echo "✅ Minikube Ingress is already enabled."
fi

# Step 10: Ensure Ingress Controller is Running
kubectl get pods -n ingress-nginx
until kubectl get pods -n ingress-nginx | grep -E "ingress-nginx-controller.*Running"; do
  echo "⏳ Waiting for ingress-nginx-controller to start..."
  sleep 5
done

echo "✅ Ingress controller is running!"

# Step 11: Deploy Ingress
kubectl rollout status deployment/ingress-nginx-controller -n ingress-nginx
kubectl apply -f k8s/ingress.yaml

# Step 12: Get Deployment Status
kubectl get pods
kubectl get services
kubectl get deployments
kubectl get ingress

echo "🚀 All services are running! 🎉"
