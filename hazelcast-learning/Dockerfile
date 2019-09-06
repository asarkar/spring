FROM azul/zulu-openjdk-alpine:11

VOLUME /tmp

COPY build/libs/*.jar /app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]