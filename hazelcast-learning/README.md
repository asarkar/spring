Hazelcast + Spring Boot + JCache caching.
==
This application demonstrates the use of JCache using Hazelcast as a provider. It can run locally in the embedded 
Hazelcast mode wheres any caches created are in the local JVM memory. It can also be deployed in a Kubernetes cluster, where
it operates in Hazelcast client mode. In client mode, the app connects to an externally running Hazelcast cluster, but
also maintains a near-cache for efficiency.

It exposes two HTTP endpoints:
* `GET /` - returns a random number that is cached. Until the cache is flushed, the same random number is returned.
* `DELETE /` - flushes the cache.

### Running Locally
Simply run the class [RandApp](src/main/kotlin/org/asarkar/cache/RandApp.kt). The app transparently determines it is
running locally and configures Hazelcast accordingly. It runs on port 8080.

### Running In Kubernetes
  1. Deploy a Hazelcast cluster by executing [deploy.sh](src/main/scripts/deploy.sh).
  2. Deploy the application by executing [deploy-client.sh](src/main/scripts/deploy-client.sh).

The endpoints are exposed through a `NodePort`, and the corresponding port is displayed in the console after client
deployment finishes.

> The deployment scripts attempt to delete any existing objects; if none exist, error messages are displayed in the
> console; such messages are non-fatal, and the deployment continues.

> If using Minikube, see [this](https://stackoverflow.com/a/42564211/839733) SO post for using local Docker images.
> However, I found [Kubernetes on Docker Desktop](https://blog.docker.com/2018/07/kubernetes-is-now-available-in-docker-desktop-stable-channel/)
> far easier to manage than Minikube.

## References
* [Devnexus 2015 - Gimme Caching, the Hazelcast JCache Way - Christoph Engelbert](https://www.youtube.com/watch?v=lQZmemBXjFE)
* [Times Table with JCache 1.1](https://github.com/hazelcast/hazelcast-code-samples/tree/master/jcache-1.1/times-table)
* [Fraud Detection near-cache example](https://hazelcast.com/blog/fraud-detection-near-cache-example/)
* [Spring Boot Support for Hazelcast](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-hazelcast.html)
* [Caching Spring Boot Microservices with Hazelcast in Kubernetes](src/site/Caching_SpringBoot_Microservices_with_Hazelcast_in_Kubernetes.pdf)
* [An Architect's View of Hazelcast IMDG](src/site/Architects_View.pdf)