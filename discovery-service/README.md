The server will start at `$DISCOVERY_HOST:$HTTP_PORT`

`DISCOVERY_HOST` is `localhost` by default.

`HTTP_PORT` is `8761` by default.

Minimal startup command:
```
(export DOCKER_MACHINE_HOST="$(echo $DOCKER_HOST | grep -Eo '([0-9]{1,3}\.){3}[0-9]{1,3}')" && \
docker run -it -p 8761:8761 \
-e DISCOVERY_HOST=$DOCKER_MACHINE_HOST \
<IMAGE ID>)
```

Look in `build.gradle` for other available environment variables.

### References

[Eureka Properties #1](http://stackoverflow.com/questions/30622904/spring-cloud-ribbon-and-https)

[Eureka Properties #2](https://github.com/spring-cloud/spring-cloud-netflix/issues/337)

[Netflix Eureka REST Operations](https://github.com/Netflix/eureka/wiki/Eureka-REST-operations)