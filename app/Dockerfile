FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/api
COPY app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
