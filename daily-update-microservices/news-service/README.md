The server will start at `$NEWS_HOST:$HTTP_PORT`

`$NEWS_HOST` is `localhost` by default.

`HTTP_PORT` is `10020` by default.

Minimal startup command:
```
(DOCKER_MACHINE_HOST="$(echo $DOCKER_HOST | grep -Eo '([0-9]{1,3}\.){3}[0-9]{1,3}')" && \
export DOCKER_MACHINE_HOST=${DOCKER_MACHINE_HOST:-localhost} && \
export ENCRYPT_KEY=<SECRET> && \
docker-compose up)
```

Look in `build.gradle` for other available environment variables.
