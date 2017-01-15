The server will start at `$MOVIE_HOST:$HTTP_PORT`

`$MOVIE_HOST` is `localhost` by default.

`HTTP_PORT` is `10010` by default.

Minimal startup command:
```
(DOCKER_MACHINE_HOST="$(echo $DOCKER_HOST | grep -Eo '([0-9]{1,3}\.){3}[0-9]{1,3}')" && \
export DOCKER_MACHINE_HOST=${DOCKER_MACHINE_HOST:-localhost} && \
export ENCRYPT_KEY=<SECRET> && \
docker-compose up)
```

Look in `build.gradle` for other available environment variables.
