FROM eclipse-temurin:21-jre-alpine

COPY app/build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
