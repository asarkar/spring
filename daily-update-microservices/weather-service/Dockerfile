FROM abhijitsarkar/docker:jdk8

ENV STARTUP_SCRIPT=/usr/local/bin/weather-service-startup.sh APP_HOME=/opt/name/abhijitsarkar/java-ee

ADD https://raw.githubusercontent.com/abhijitsarkar/java-ee/master/build-tools/startup.sh $STARTUP_SCRIPT

RUN chmod +x $STARTUP_SCRIPT

WORKDIR $APP_HOME

ADD build/libs/*.jar weather-service.jar

ENTRYPOINT ["/usr/local/bin/weather-service-startup.sh"]
CMD ["-a", "config-service", "/opt/name/abhijitsarkar/java-ee/weather-service.jar"]