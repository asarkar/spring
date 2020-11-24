##### Build Application Docker Image
```
redis-learning$ ./gradlew clean bootBuildImage
```

##### Start standalone Redis server
````
redis-learning$ docker run -d --rm --name redis -p 6379:6379 -e ALLOW_EMPTY_PASSWORD=yes bitnami/redis
````

##### Connect to Redis CLI
```
redis-learning$ docker exec -it <container name or id> redis-cli
```

##### Authenticate on Redis CLI
```
127.0.0.1:6379> AUTH <password>
```

##### Start Redis replica
```
redis-learning$ docker-compose -f redis-replica.yaml up -d
```

##### Get replication info
```
127.0.0.1:6379> INFO replication
```

##### Start Redis cluster
```
redis-learning$ docker-compose -f redis-cluster.yaml up -d
```

##### Get cluster info
```
127.0.0.1:6379> CLUSTER NODES
```
https://redis.io/commands/cluster-nodes

##### Start Redis sentinels
```
redis-learning$ docker-compose -f redis-sentinel.yaml up --scale redis-sentinel=3 -d
```
> `--scale` may be added for each service.

##### Connect to a sentinel
```
redis-learning$ docker exec -it redis-learning_redis-sentinel_1 redis-cli -p 26379
```

##### Get master info
```
127.0.0.1:26379> sentinel masters
```

##### Check connectivity to master from a sentinel
```
I have no name!@b10e70d4eaba:/$ nc -vz -w 1 redis-master 6379
```

##### Create a record
```
redis-learning$ curl -H "Content-Type: application/json" \
  -X POST \
  --data '{"firstName": "John", "lastName": "Doe"}' \
  "http://localhost:8080/persons"
```

##### Find a record by id
```
redis-learning$ curl "http://localhost:8080/persons/{id}"
```

##### Find container IP
```
redis-learning$ docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <container name or id>
```

##### Execute command on container
```
redis-learning$ docker exec -it <container name or id> sh -c "<command>"
```