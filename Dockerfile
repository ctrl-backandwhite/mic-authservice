FROM eclipse-temurin:21-jre

WORKDIR /app

ARG JAR_FILE=target/mic-authservice-*.jar
COPY ${JAR_FILE} app.jar

ENV PROFILE=local \
    JAVA_OPTS=""

EXPOSE 6001 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
