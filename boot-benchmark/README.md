boot-benchmark
===
Microbenchmark Spring Boot application startup using JMH.

### Run Locally

1. Build Shadow JAR
```
$ ./gradlew clean shadowJar
```

2. Run benchmark; JMH options may be passed as arguments (`-h` for help)
```
$ java -jar minimal-benchmark/build/libs/minimal-benchmark-0.0.1-SNAPSHOT-all.jar \
  -bm avgt -f 2 -foe true -i 5 -wi 1
```
