#!/bin/bash

# continue on error
set +e

kubectl delete services/hazelcast
kubectl delete statefulsets/hazelcast
kubectl create -f k8s/hazelcast.yaml
kubectl get po -l app=hazelcast