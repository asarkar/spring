**Start Databases**

```
docker run -d --name cb --rm -p 8091-8094:8091-8094 -p 11210:11210 couchbase
docker run --name mysql --rm -p 3306:3306 -e MYSQL_DATABASE=beer_demo -e MYSQL_ROOT_PASSWORD=beer -d mysql
```

**Connect to MySQL Console** (if desired)
```
docker exec -it mysql sh -c 'exec mysql -uroot -pbeer'
```

**Run App**
```
beer-demo$ ./gradlew clean bootRun -Dspring.profiles.active=[couchbase|jpa|couchbase,jpa]
```

**Find a Beer**
```
beer-demo$ curl -H "Accept: application/json" "http://localhost:8080/beers/Double%20Trouble%20IPA"
```

**Find a Brewery**
```
beer-demo$ curl -H "Accept: application/json" "http://localhost:8080/breweries/21st%20Amendment%20Brewery%20Cafe"
```
