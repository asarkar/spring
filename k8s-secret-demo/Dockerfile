FROM openjdk:8u141-jdk-slim

RUN mkdir -p /root/.ssh && \
  chmod 700 /root/.ssh
COPY id_rsa /root/.ssh/id_rsa
COPY known_hosts /root/.ssh/known_hosts
RUN chmod -R 600 /root/.ssh/*

COPY build/libs/*.jar /opt/k8s-secret-demo.jar
EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "/opt/k8s-secret-demo.jar"]
