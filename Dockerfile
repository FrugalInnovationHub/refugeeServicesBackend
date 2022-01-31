FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 443
EXPOSE 8443
EXPOSE 80
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]