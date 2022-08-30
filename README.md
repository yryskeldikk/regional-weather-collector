# Deployment
## Build jar 
```bash
mvn package
```

## Build Docker Image
1. Ensure the following variables are defined and assigned.
   DOCKER_REPO => assigned with the local docker repository IP and Port
   VERSION => assigned with the gitlab tag found for this project
   ENV => available values: dm-dev, dev, uat, dr, prod. [ must ensure lower case ]

### Set following env variables
```bash
export DOCKER_REPO=???
export VERSION=???
export ENV=???          # [dm-dev, dev, uat, dr, prod]
```
2. Build the docker image and make available for local kubernetes to download
### Build and push an image
```bash
docker build -t $DOCKER_REPO/regional-weather-collector:$VERSION
docker push $DOCKER_REPO/regional-weather-collector:$VERSION
```
3. Deploy the deployment into k8s
### Deploy into k8s
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