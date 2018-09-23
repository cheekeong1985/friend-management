FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=/target/friend-management-app.jar
ADD ${JAR_FILE} friend-management-app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/friend-management-app.jar"]