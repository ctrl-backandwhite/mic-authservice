FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=target/mic-authservice-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
