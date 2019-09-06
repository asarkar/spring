#!/bin/bash

# continue on error
set +e

kubectl delete services/hazelcast-client
kubectl delete deployments/hazelcast-client
./gradlew clean build
docker build -t hazelcast-client .
kubectl create -f k8s/hazelcast-client.yaml
kubectl get services -l app=hazelcast-client
kubectl get po -l app=hazelcast-client