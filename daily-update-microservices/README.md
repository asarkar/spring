### Movies

Provider: http://api.themoviedb.org

   * Get popular movies:
     - Direct: http:\<HOST\>:10020/movies/popular
     - Daily update: http:\<HOST\>:10050/dailyupdate/movies/popular
     - Gateway: http:\<HOST\>:8080/[dailyupdate/]movies/popular

### News

Provider: http://api.nytimes.com

   * Get top stories:
     - Direct: http:\<HOST\>:10020/news/topstories[?sections=world,home,...]
     - Daily update: http:\<HOST\>:10050/dailyupdate/news/topstories[?sections=world,home,...]
     - Gateway: http:\<HOST\>:8080/[dailyupdate/]news/topstories[?sections=world,home,...]

   List of [all sections](http://developer.nytimes.com/docs/read/top_stories_api)

### Weather

Provider: http://openweathermap.org/

   * Get weather by zip code:
     - Direct: http:\<HOST\>:10030/weather/zipCode/{zipCode}
     - Daily update: http:\<HOST\>:10050/dailyupdate/weather/zipCode/{zipCode}
     - Gateway: http:\<HOST\>:8080/[dailyupdate/]weather/zipCode/{zipCode}

### Dev Notes
   * Full-strength JCE must be installed to use encryption
   * [Spring Cloud Integration Tests](https://github.com/spring-cloud-samples/tests)
   * Setting breakpoint in `feign.SynchronousMethodHandler.executeAndDecode()`
   shows the request made and response received, in one place.

Minimal discovery and config startup command:
```
(DOCKER_MACHINE_HOST="$(echo $DOCKER_HOST | grep -Eo '([0-9]{1,3}\.){3}[0-9]{1,3}')" && \
export DOCKER_MACHINE_HOST=${DOCKER_MACHINE_HOST:-localhost} && \
export ENCRYPT_KEY=<SECRET> && \
docker-compose up)
```

   * Hazelcast Management Center a.k.a Mancenter:

Hazelcast offers a Mancenter war that shows real time info of available clusters and data structures.
The app needs to be told to connect to the Mancenter at start up. See `commons`:`CommonConfig` for the necessary parameters.

The Mancenter is available as a war with Hazelcast download and is started with `java -jar <WAR NAME> <PORT> mancenter`.
Default log in username/password is `admin/admin`. It, of course, can also be ran as a Docker image.

   * Spring Session
Spring Session is enabled with `auth-service` and configured to use Hazelcast. A `curl` request/response may look as follows:
```
* Connected to localhost (::1) port 8080 (#0)
* Server auth using Basic with user 'abhijitsarkar'
> GET /movies/popular HTTP/1.1
> Host: localhost:8080
> Authorization: Basic YWJoaWppdHNhcmthcjpzZWNyZXQ=
> User-Agent: curl/7.43.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Server: Apache-Coyote/1.1
< X-Application-Context: gateway-service:8080
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< x-auth-token: e8e27ffe-a75e-422b-aa67-8cf928dbcccc
< Date: Thu, 07 Jan 2016 06:58:25 GMT
< Date: Thu, 07 Jan 2016 06:58:25 GMT
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
```

The line of interest is <mark>x-auth-token: e8e27ffe-a75e-422b-aa67-8cf928dbcccc</mark>. This is the session id. The
session is saved in a `SessionRepository`, which in this case is Hazelcast. If we log into Hazelcast Mancenter, we can
see a map by the name `spring:session:sessions`. If subsequent requests are made with this session id as a header,
the same session is used and the client maintains a conversational state with the server until the session expires.
```
 Connected to localhost (::1) port 8080 (#0)
* Server auth using Basic with user 'abhijitsarkar'
> GET /movies/popular HTTP/1.1
> Host: localhost:8080
> Authorization: Basic YWJoaWppdHNhcmthcjpzZWNyZXQ=
> User-Agent: curl/7.43.0
> Accept: */*
> x-auth-token: e8e27ffe-a75e-422b-aa67-8cf928dbcccc
>
< HTTP/1.1 200 OK
```

