FROM openjdk:8u131-jdk-alpine
COPY build/libs/*.jar /app.jar
RUN touch /app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
