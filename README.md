# Deployment
## Set following env variables
```bash
export DOCKER_REPO=???
export COLLECTOR_NAME=??? 
export VERSION=???
```

## Build
1. Indicate the correct collector in the application.properties
2. Build the deployment
```bash
docker build -t $DOCKER_REPO/$COLLECTOR_NAME:$VERSION
docker push $DOCKER_REPO/$COLLECTOR_NAME:$VERSION
```
3. Deploy the deployment into k8s
```bash
envsubst < k8s_deploy.template.yaml >k8s_deploy.yaml
kubectl create -f k8s_deploy.yaml
```

# Data Collector Info
```bash
Air Temperature Data Collector (every 10 min)
Humidity Data Collector (every 10 min)
Wind Data Collector (every 10 min)
```