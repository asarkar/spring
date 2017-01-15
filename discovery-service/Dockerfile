FROM abhijitsarkar/docker:jdk8

ENV INIT_SCRIPT=/usr/local/bin/discovery-service-startup.sh APP_HOME=/opt/name/abhijitsarkar/java-ee

ADD https://raw.githubusercontent.com/abhijitsarkar/java-ee/master/build-tools/startup.sh $INIT_SCRIPT

RUN chmod +x $INIT_SCRIPT

WORKDIR $APP_HOME

ADD build/libs/*.jar discovery-service.jar

ENTRYPOINT ["/usr/local/bin/discovery-service-startup.sh"]
CMD ["/opt/name/abhijitsarkar/java-ee/discovery-service.jar"]