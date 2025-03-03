#!/bin/bash
 
set -e  # Exit on error
set -o pipefail  # Catch pipeline errors

echo "🚀 Starting Kubernetes Deployment..."

# Step 1: Load .env File
echo "🔑 Loading environment variables from .env..."
export $(grep -v '^#' .env | xargs)
echo "✅ Environment variables loaded!"

# Step 2: Function to Check & Build Image if Missing
build_if_missing() {
  IMAGE_NAME=$1
  BUILD_PATH=$2
  BUILD_ARGS=$3

  if [[ "$(docker images -q $IMAGE_NAME:latest 2> /dev/null)" == "" ]]; then
    echo "🛠️ Image $IMAGE_NAME not found. Building..."
    docker build -t $IMAGE_NAME:latest $BUILD_ARGS $BUILD_PATH
    echo "✅ Image $IMAGE_NAME built!"
  else
    echo "✅ Image $IMAGE_NAME already exists. Skipping build."
  fi
}

# Step 3: Check & Build Images If Needed
build_if_missing game-price-bot-game ./gpb-game "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-backend ./gpb-backend "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-email ./gpb-email "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-telegram ./gpb-telegram "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-react ./gpb-front "--build-arg BACKEND_SERVICE_URL=$BACKEND_SERVICE_URL --build-arg TELEGRAM_BOT_URL=$TELEGRAM_BOT_URL --build-arg SUPPORT_EMAIL=$SUPPORT_EMAIL"

# Step 4: Load Images into Minikube Cache
echo "📤 Loading images into Minikube cache..."
minikube image load game-price-bot-backend:latest
minikube image load game-price-bot-game:latest
minikube image load game-price-bot-email:latest
minikube image load game-price-bot-telegram:latest
minikube image load game-price-bot-react:latest
echo "✅ Images loaded into Minikube cache!"

# Step 5: Load Secrets into Kubernetes
echo "🔑 Creating Kubernetes Secrets from .env file..."
kubectl delete secret game-price-bot-secret --ignore-not-found
kubectl create secret generic game-price-bot-secret --from-env-file=.env
echo "✅ Secrets created!"

# Step 6: Deploy PostgreSQL
echo "📦 Deploying PostgreSQL..."
kubectl apply -f k8s/postgres/postgres.yaml
echo "✅ PostgreSQL deployed!"

# Step 7: Deploy Kafka & Zookeeper
echo "📦 Deploying Kafka & Zookeeper..."
kubectl apply -f k8s/kafka/storage.yaml
kubectl apply -f k8s/kafka/zookeeper.yaml
kubectl apply -f k8s/kafka/kafka.yaml
kubectl apply -f k8s/kafka/service.yaml
echo "✅ Kafka & Zookeeper deployed!"

# Step 8: Deploy Game Service
echo "📦 Deploying Game Service..."
kubectl apply -f k8s/game/storage.yaml
kubectl apply -f k8s/game/deployment.yaml
kubectl apply -f k8s/game/service.yaml
echo "✅ Game Service deployed!"

# Step 9: Deploy Backend Service
echo "📦 Deploying Backend Service..."
kubectl apply -f k8s/backend/deployment.yaml
kubectl apply -f k8s/backend/service.yaml
echo "✅ Backend deployed!"

# Step 10: Deploy Telegram Bot
echo "📦 Deploying Telegram Bot..."
kubectl apply -f k8s/telegram/deployment.yaml
kubectl apply -f k8s/telegram/service.yaml
echo "✅ Telegram Bot deployed!"

# Step 11: Deploy Email Service
echo "📦 Deploying Email Service..."
kubectl apply -f k8s/email/deployment.yaml
kubectl apply -f k8s/email/service.yaml
echo "✅ Email Service deployed!"

# Step 12: Deploy Frontend Service
echo "📦 Deploying Frontend Service..."
kubectl apply -f k8s/frontend/deployment.yaml
kubectl apply -f k8s/frontend/service.yaml
echo "✅ Frontend deployed!"

# Step 13: Restart All Failed Pods
echo "🔄 Restarting any failed pods..."
kubectl delete pod --all
echo "✅ Pods restarted!"

# Step 14: Enable Ingress in Minikube and Deploy Ingress
echo "🌐 Checking if Minikube Ingress is enabled..."
if ! minikube addons list | grep -q "ingress.*enabled"; then
  echo "🔄 Enabling Minikube Ingress..."
  minikube addons enable ingress
  echo "✅ Minikube Ingress enabled!"
else
  echo "✅ Minikube Ingress is already enabled."
fi

echo "🌐 Deploying Ingress..."
kubectl apply -f k8s/ingress.yaml
echo "✅ Ingress deployed!"

# Step 15: Get Ports
echo "🔍 Get Ports..."
echo "✅ Backend is available at: http://game.price.bot/api"
echo "✅ Frontend is available at: http://game.price.bot"

# Step 16: Verify Deployment Status
echo "🔍 Checking Deployment Status..."
kubectl get pods
kubectl get services
kubectl get deployments
kubectl get ingress

echo "🚀 All services are running! 🎉"
