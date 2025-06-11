#!/bin/bash
if [[ "$OSTYPE" == "win32" ]]; then
  echo "⚠️ Run this script using Git Bash or WSL, not CMD or PowerShell."
  exit 1
fi


set -e  # Exit on error
set -o pipefail  # Catch pipeline errors

echo "🚀 Starting Kubernetes Deployment..."

 # Step 1: ensure namespaces exist
 if ! kubectl get namespace monitoring >/dev/null 2>&1; then
   echo "📁 Namespace 'monitoring' not found—creating it now…"
   kubectl create namespace monitoring
 fi

if ! kubectl get namespace game-price-bot >/dev/null 2>&1; then
  echo "📁 Namespace 'game-price-bot' not found—creating it now…"
  kubectl create namespace game-price-bot
fi

# Step 2: Load .env File
if [ -f .env ]; then
  echo "🔑 Loading environment variables from .env..."
  while IFS='=' read -r key value; do
    if [[ "$key" =~ ^\s*# ]] || [[ -z "$key" ]]; then
      continue
    fi
    value="${value%\"}"
    value="${value#\"}"
    value="${value//$'\r'/}"
    export "$key=$value"
  done < .env
  echo "✅ Environment variables loaded!"
else
  echo "❌ .env file not found! Exiting..."
  exit 1
fi

kubectl config use-context minikube

# Step 3: Switch Docker Context
if docker context ls | grep -q "minikube"; then
  docker context use minikube
  echo "✅ Using Minikube Docker context!"
else
  docker context use default
  echo "⚠️ Minikube context not found, using default."
fi

# Step 4: Check & Build Images If Needed
build_if_missing() {
  IMAGE_NAME=$1
  BUILD_PATH=$2
  BUILD_ARGS=$3

  if [[ -z $(docker images -q -f "reference=ghcr.io/illusiveman2186/$IMAGE_NAME:latest") ]]; then
    echo "🛠 Image $IMAGE_NAME not found. Building..."
    docker build --no-cache -t ghcr.io/illusiveman2186/$IMAGE_NAME:latest $BUILD_ARGS $BUILD_PATH
    echo "✅ Image $IMAGE_NAME built!"
  else
    echo "✅ Image $IMAGE_NAME already exists. Skipping build."
  fi
}

build_if_missing game-price-bot-game ./gpb-game "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-backend ./gpb-backend "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-email ./gpb-email "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-telegram ./gpb-telegram "--build-arg DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL --build-arg DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME --build-arg DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD"
build_if_missing game-price-bot-react ./gpb-front "--build-arg BACKEND_SERVICE_URL=$BACKEND_SERVICE_URL --build-arg TELEGRAM_BOT_URL=$TELEGRAM_BOT_URL --build-arg SUPPORT_EMAIL=$SUPPORT_EMAIL"

# Step 5: Load Images into Minikube Cache
if minikube status > /dev/null 2>&1; then
  echo "📤 Loading images into Minikube cache..."
  for image in game-price-bot-backend game-price-bot-game game-price-bot-email game-price-bot-telegram game-price-bot-react; do
    echo "Load image:$image into Minikube cache"
    minikube image load ghcr.io/illusiveman2186/$image:latest
  done
  echo "✅ Images loaded into Minikube cache!"
else
  echo "❌ Minikube is not running! Skipping image load..."
fi

# Step 6: Load Secrets into Kubernetes
echo "🛠 Create secrets!"

kubectl delete secret game-price-bot-secret -n game-price-bot --ignore-not-found
kubectl delete secret game-price-bot-secret -n monitoring    --ignore-not-found

kubectl create secret generic game-price-bot-secret \
  -n game-price-bot \
  --from-env-file=.env
kubectl create secret generic game-price-bot-secret \
  -n monitoring \
  --from-literal=admin-user=admin \
  --from-literal=GRAFANA_ADMIN_PASSWORD="$GRAFANA_ADMIN_PASSWORD"

echo "✅ Secrets created!"

# Step 7: Add Helm Repository
echo "📦 Adding Helm repositories..."

helm repo add grafana https://grafana.github.io/helm-charts || true
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx || true
helm repo update

echo "✅ Helm repositories are ready!"

# Step 8: Create Promtail ConfigMap
echo "🛠 Installing Promtail via Helm..."

helm upgrade --install loki-stack grafana/loki-stack \
  --namespace monitoring \
  -f  k8s/observability/values.yaml

echo "✅ Promtail is ready!"

# Step 9: Deploy Kubernetes Services
echo "🛠 Deploying services ..."

kubectl apply -n game-price-bot -f k8s/postgres/postgres.yaml
kubectl apply -n game-price-bot -f k8s/kafka/
kubectl apply -n game-price-bot -f k8s/game/
kubectl apply -n game-price-bot -f k8s/backend/
kubectl apply -n game-price-bot -f k8s/telegram/
kubectl apply -n game-price-bot -f k8s/email/
kubectl apply -n game-price-bot -f k8s/frontend/

echo "✅ All services deployed!"

# Step 10: Restart Deployments Without Removing Pods
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
  kubectl rollout restart deployment/$deployment_name -n game-price-bot
  kubectl rollout status deployment/$deployment_name -n game-price-bot
done

# Step 11: Enable Ingress via Helm
echo "🔄 Installing ingress-nginx via Helm with NodePort + no webhooks…"
helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx --create-namespace \
  --version 4.7.1 \
  --set controller.service.type=LoadBalancer \
  --set controller.admissionWebhooks.enabled=false \
  --timeout 10m || echo "⚠️ Helm timed out, will wait manually..."

echo "✅ Ingress-nginx installed via Helm !"

# Step 12: Waiting for ingress-nginx-controller pod
echo "⏳ Waiting for ingress-nginx-controller pod to be ready (via kubectl wait)..."

kubectl wait --namespace ingress-nginx \
  --for=condition=Ready pod \
  -l app.kubernetes.io/component=controller \
  --timeout=1200s || {
    echo "❌ ingress-nginx-controller pod did not become ready in time. Exiting..."
    exit 1
  }

echo "✅ ingress-nginx-controller pod is ready!"

# Step 13: Deploy Cert-Manager + ClusterIssuer
echo "📦 Deploying cert-manager..."
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/latest/download/cert-manager.yaml

echo "⏳ Waiting for cert-manager-webhook deployment to complete..."
kubectl rollout status deployment cert-manager-webhook -n cert-manager

echo "⏳ Verifying cert-manager webhook TLS availability..."
until kubectl get validatingwebhookconfiguration cert-manager-webhook -o jsonpath='{.webhooks[0].clientConfig.caBundle}' | grep -q '[A-Za-z0-9+/=]\{100,\}'; do
  echo "🔁 Waiting for cert-manager webhook CA to be ready..."
  sleep 2
done
echo "✅ cert-manager webhook TLS ready."

echo "📦 Applying ClusterIssuer..."
kubectl apply -f k8s/ingress/clusterissuer.yaml

echo "📦 Applying Certificate..."
kubectl apply -f k8s/ingress/certificate.yaml

# Step 14: Deploy Ingress Resource
echo "🌐 Applying Ingress definition..."
kubectl apply -f k8s/ingress/ingress.yaml
kubectl apply -f k8s/ingress/grafana-ingress.yaml

# Step 15: Show Deployment Status
echo "📊 Current cluster state:"
kubectl get pods -n game-price-bot
kubectl get services -n game-price-bot
kubectl get deployments -n game-price-bot
kubectl get ingress -n game-price-bot
kubectl get ingress -n monitoring

echo "🚀 All services are running with HTTPS on game.price.bot! 🎉"

