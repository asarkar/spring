FROM abhijitsarkar/docker:jdk8

ENV INIT_SCRIPT=/usr/local/bin/config-service-startup.sh APP_HOME=/opt/name/abhijitsarkar/java-ee

ADD https://raw.githubusercontent.com/abhijitsarkar/java-ee/master/build-tools/startup.sh $INIT_SCRIPT

RUN chmod +x $INIT_SCRIPT

WORKDIR $APP_HOME

ADD build/libs/*.jar config-service.jar

ENTRYPOINT ["/usr/local/bin/config-service-startup.sh"]
CMD ["/opt/name/abhijitsarkar/java-ee/config-service.jar"]