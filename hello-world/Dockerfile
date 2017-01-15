FROM abhijitsarkar/docker:tomcat8

ENV INIT_SCRIPT /usr/bin/init.sh

ADD build/libs/*.war $CATALINA_HOME/webapps/helloworld.war

ADD ./init.sh $INIT_SCRIPT

RUN chmod +x $INIT_SCRIPT

ENTRYPOINT ["/usr/bin/init.sh"]
