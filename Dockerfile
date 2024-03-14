FROM openjdk:21-jdk

RUN  mkdir -p /etc/benefitalign/event-streamify

WORKDIR /etc/benefitalign/event-streamify

ADD build/libs/*.jar ./

CMD java $JAVA_OPTS -Dserver.port=8080 -Dmicronaut.environments=test -jar *.jar

EXPOSE 8080
