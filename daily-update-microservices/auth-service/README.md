The server will start at `$AUTH_HOST:$HTTP_PORT`

`$AUTH_HOST` is `localhost` by default.

`HTTP_PORT` is `9000` by default.

Minimal startup command:
```
(DOCKER_MACHINE_HOST="$(echo $DOCKER_HOST | grep -Eo '([0-9]{1,3}\.){3}[0-9]{1,3}')" && \
export DOCKER_MACHINE_HOST=${DOCKER_MACHINE_HOST:-localhost} && \
docker run -it -p 9000:9000 \
-e DISCOVERY_HOST=$DOCKER_MACHINE_HOST \
-e CONFIG_HOST=$DOCKER_MACHINE_HOST \
-e AUTH_HOST=$DOCKER_MACHINE_HOST \
<IMAGE ID>)
```

Look in `build.gradle` for other available environment variables.
