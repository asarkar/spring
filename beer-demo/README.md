**Start Couchbase**

See https://github.com/asarkar/docker/tree/master/couchbase.

> Official Couchbase requires [manual set up]((https://hub.docker.com/r/couchbase/server/)), thus I created
my own image that's development ready out of the box.

**Add an entry to your hosts file**
```
127.0.0.1       couchbase-node-0.couchbase
```

**Run App**
```
beer-demo$ ./gradlew clean bootRun \
    -Dcouchbase.bucket.createIfMissing=true \
    -Dcouchbase.dnsSrvEnabled=false \
    -Dbeer-demo.initialize=true
```

**Find a Beer**
```
beer-demo$ curl -H "Accept: application/json" "http://localhost:8080/beers/Double%20Trouble%20IPA"
```

**Find a Brewery**
```
beer-demo$ curl -H "Accept: application/json" "http://localhost:8080/breweries/21st%20Amendment%20Brewery%20Cafe"
```

**Find All Beers**
```
beer-demo$ curl -H "Accept: application/json" "http://localhost:8080/beers"
```

**Find All Breweries**
```
beer-demo$ curl -H "Accept: application/json" "http://localhost:8080/breweries"
```
