**Start Couchbase**

```
docker run -d --name couchbase -p 8091-8094:8091-8094 -p 11210:11210 asarkar/couchbase:debian-jessie
```

> Official Couchbase requires [manual set up]((https://hub.docker.com/r/couchbase/server/)), thus I customized
my own image that's development ready out of the box.

**Run App**
```
beer-demo$ ./gradlew clean bootRun [-Dcouchbase.initialize=true|false]
```

**Find a Beer**
```
beer-demo$ curl -H "Accept: application/json" "http://localhost:8080/beers/Double%20Trouble%20IPA"
```

**Find a Brewery**
```
beer-demo$ curl -H "Accept: application/json" "http://localhost:8080/breweries/21st%20Amendment%20Brewery%20Cafe"
```
