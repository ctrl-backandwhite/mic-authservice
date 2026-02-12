FROM eclipse-temurin:21-jdk-alpine

ENV PROFILE=dev

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 6002
ENTRYPOINT ["java","-jar","app.jar"]
