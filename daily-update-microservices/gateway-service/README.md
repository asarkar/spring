The server will start at `$GATEWAY_HOST:$HTTP_PORT`

`$GATEWAY_HOST` is `localhost` by default.

`HTTP_PORT` is `8080` by default.

Minimal startup command:
```
(DOCKER_MACHINE_HOST="$(echo $DOCKER_HOST | grep -Eo '([0-9]{1,3}\.){3}[0-9]{1,3}')" && \
export DOCKER_MACHINE_HOST=${DOCKER_MACHINE_HOST:-localhost} && \
docker run -it -p 8080:8080 \
-e DISCOVERY_HOST=$DOCKER_MACHINE_HOST \
-e CONFIG_HOST=$DOCKER_MACHINE_HOST \
-e GATEWAY_HOST=$DOCKER_MACHINE_HOST \
<IMAGE ID>)
```

Look in `build.gradle` for other available environment variables.
