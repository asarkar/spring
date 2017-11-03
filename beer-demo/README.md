**Start Couchbase**

See https://github.com/asarkar/docker/tree/master/couchbase.

> Official Couchbase requires [manual set up]((https://hub.docker.com/r/couchbase/server/)), thus I created
my own image that's development ready out of the box.

**Build Docker Image**
```
beer-demo$ docker build -t asarkar/couchbase-client -f couchbase-client/docker/Dockerfile couchbase-client
```

**Create a Deployment**
```
beer-demo$ kubectl create -f couchbase-client/k8s/deployment.yaml
```

**Expose the Deployment**
```
beer-demo$ kubectl expose deployment couchbase-client --type=NodePort --name=couchbase-client
```

Follow [this](https://kubernetes.io/docs/tasks/access-application-cluster/service-access-application-cluster/) article to get the `NodePort` and public node IP.

**Find a Beer**
```
beer-demo$ curl -H "Accept: application/json" "http://<public-node-ip>:<node-port>/beers/Double%20Trouble%20IPA"
```

**Find a Brewery**
```
beer-demo$ curl -H "Accept: application/json" "http://<public-node-ip>:<node-port>/breweries/21st%20Amendment%20Brewery%20Cafe"
```

**Find All Beers**
```
beer-demo$ curl -H "Accept: application/json" "http://<public-node-ip>:<node-port>/beers"
```

**Find All Breweries**
```
beer-demo$ curl -H "Accept: application/json" "http://<public-node-ip>:<node-port>/breweries"
```
