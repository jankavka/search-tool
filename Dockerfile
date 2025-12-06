FROM amazoncorretto:17.0.7-alpine

WORKDIR /app

COPY target/SearchToolServer-1.0-SNAPSHOT.jar app.jar

COPY src/main/resources/** resources/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

