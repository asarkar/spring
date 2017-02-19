1. Start Kafka with ZooKeeper
   ```
   # 2181 is zookeeper, 9092 is kafka
   asarkar:~$ docker run --name kafka -d -p 2181:2181 -p 9092:9092 \
   --env ADVERTISED_HOST=127.0.0.1 --env ADVERTISED_PORT=9092 \
   asarkar/kafka
   ```

2. Create topic
   ```
   asarkar:~$ docker exec -it kafka /opt/kafka_2.11-0.10.1.0/bin/kafka-topics.sh \
   --zookeeper localhost:2181 \
   --create --topic ufo --partitions 2 --replication-factor 1
   ```

3. Run `UfoApplication` and await termination.

> Gets data from: http://www.nuforc.org/webreports.html

To delete topic

```
asarkar:~$ docker exec -it kafka /opt/kafka_2.11-0.10.1.0/bin/kafka-topics.sh \
--zookeeper localhost:2181 \
--delete --topic ufo
```

> On restart, the consumers start reading from the beginning. Thus, if the queue is not cleaned up, the analytics
is going to be incorrect.
