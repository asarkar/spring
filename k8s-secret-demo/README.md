# Commands

## Minikube

**Start Minikube**: `minikube start --vm-driver=virtualbox --insecure-registry="<domain>"`

**Stop Minikube**: `minikube stop`

**Launch the application**: `minikube service k8s-secret-demo`

## Kubectl

### Managing secrets

**Create secret from literal**: `kubectl create secret generic demo-secret --from-literal=k8s.demo.secret=whatever`

**Get secret**: `kubectl get secret demo-secret -o json | jq '.data."k8s.demo.secret"' | sed -e "s/\"//g" | base64 --decode`

> Above command uses `jq` (`brew install jq`) to parse the JSON response.

**Delete secret**: `kubectl delete secret demo-secret`

**Create secret from file**
```
kubectl create secret generic ssh-secret \
  --from-file=id_rsa=/Users/abhijit_sarkar/.ssh/id_rsa \
  --from-file=id_rsa.pub=/Users/abhijit_sarkar/.ssh/id_rsa.pub \
  --from-file=known_hosts=/Users/abhijit_sarkar/.ssh/known_hosts
```

### Managing deployments

**Create deployment**: `kubectl create -f deployment.yaml`

**Expose deployment**: `kubectl expose deployment k8s-secret-demo --type=LoadBalancer`

**Delete deployment (and Docker image)**
```
kubectl delete deployment k8s-secret-demo &amp;&amp; \
  docker rmi -f $(docker images k8s-secret-demo --format {{.ID}})
```

### Building the app

```
./gradlew clean bootJar &amp;&amp; \
  docker build -t k8s-secret-demo .
```

# References

- [Can't pull images from an insecure registry in Minikube VM](https://github.com/kubernetes/minikube/issues/604)

> There is a JSON file created after the Minikube VM is created (I used virtualbox on Linux) -
  `$HOME/.minikube/machines/minikube/config.json` - that contains a lot of config for the VM.
  
> In this, you can see some interesting config under `HostOptions` -> `EngineOptions` - including the `InsecureRegistry`.
  
> You can edit this JSON array while your minikube VM is stopped. And, once restarted, it seems to take effect - without deleting the VM.

- [Secret](https://kubernetes.io/docs/concepts/configuration/secret/)

- [Hello Minikube](https://kubernetes.io/docs/tutorials/stateless-application/hello-minikube/)

- [Running Kubernetes Locally via Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/)