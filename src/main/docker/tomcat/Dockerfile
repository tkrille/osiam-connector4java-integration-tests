FROM tomcat:8-jre8

COPY osiam-resource-server $CATALINA_HOME/webapps/osiam-resource-server
COPY osiam-auth-server $CATALINA_HOME/webapps/osiam-auth-server
COPY addon-self-administration $CATALINA_HOME/webapps/addon-self-administration
COPY addon-self-administration-plugin-example.jar /install/plugin-example.jar
COPY selfadminexample.properties /install/
COPY manager $CATALINA_HOME/webapps/manager
ADD http://central.maven.org/maven2/com/icegreen/greenmail-webapp/1.4.0/greenmail-webapp-1.4.0.war $CATALINA_HOME/webapps/

RUN cat /install/selfadminexample.properties >> $CATALINA_HOME/conf/catalina.properties
RUN sed -i 's/8080/8180/g' $CATALINA_HOME/conf/server.xml
RUN cp -p /usr/share/zoneinfo/${user.timezone} /etc/localtime
RUN echo "${user.timezone}" > /etc/timezone

EXPOSE 8180
