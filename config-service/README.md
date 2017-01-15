The server will start at `$CONFIG_HOST:$HTTP_PORT`

`CONFIG_HOST` is `localhost` by default.

`HTTP_PORT` is `8888` by default.

Minimal startup command:
```
(DOCKER_MACHINE_HOST="$(echo $DOCKER_HOST | grep -Eo '([0-9]{1,3}\.){3}[0-9]{1,3}')" && \
export DOCKER_MACHINE_HOST=${DOCKER_MACHINE_HOST:-localhost} && \
docker run -it -p 8888:8888 \
-e CONFIG_HOST=$DOCKER_MACHINE_HOST \
-e DISCOVERY_HOST=$DOCKER_MACHINE_HOST \
-e SPRING_PROFILES_ACTIVE=native \
-e CONFIG_LOCATION=<CONFIG LOCATION> \
-e ENCRYPT_KEY=<SECRET> \
-v <HOST LOCATION>:<CONFIG LOCATION>
<IMAGE ID>)
```
Look in `build.gradle` for other available environment variables.

**An encrypt key must be provided. Note that full-strength JCE must be installed.**

On OS X, the OS may flag the files so that it can ask for user confirmation the first time the downloaded program is run,
to help stop malware. In that case, `ls -al` shows the files as below, with a `@`, which means the file
has extended attributes. The extended attributes can be viewed with `ls -al@`.
```
-rw-r--r--@ 1 root  wheel  3023 Dec 20  2013 US_export_policy.jar
	com.apple.quarantine	  68
-rw-r--r--@ 1 root  wheel  3035 Dec 20  2013 local_policy.jar
	com.apple.quarantine	  68
```

In order to force remove the `@` flag, use `xattr`:
```
sudo xattr -d com.apple.quarantine US_export_policy.jar
```

To encrypt using curl `curl http://<CONFIG HOST>:<CONFIG PORT>/encrypt --data-urlencode <PLAIN TEXT>`
To decrypt using curl `curl http://<CONFIG HOST>:<CONFIG PORT>/decrypt --data-urlencode <ENCRYPTED>`
