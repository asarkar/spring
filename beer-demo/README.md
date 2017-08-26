```
docker run -d --name cb --rm -p 8091-8094:8091-8094 -p 11210:11210 couchbase
docker run --name mysql --rm -p 3306:3306 -e MYSQL_DATABASE=beer_demo -e MYSQL_ROOT_PASSWORD=beer -d mysql
docker exec -it mysql sh -c 'exec mysql -uroot -pbeer'
```