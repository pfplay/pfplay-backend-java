FROM eclipse-temurin:17-jdk-alpine AS builder
#FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /opt/pfplay
COPY . .

RUN ./gradlew clean build -x test --refresh-dependencies

ARG USERNAME=pfplay
ARG USER_UID=1000
ARG USER_GID=$USER_UID

FROM eclipse-temurin:17-jdk-alpine
ENV JAR_PATH=/opt/api/pfplay-api-v1.jar
WORKDIR /opt/api
COPY --from=builder /opt/pfplay/build/libs/*.jar ${JAR_PATH}
ENTRYPOINT ["sh", "-c", "java -jar ${JAR_PATH}"]
