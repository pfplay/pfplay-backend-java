#FROM eclipse-temurin:17-jdk-alpine as build
##FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine as build
#WORKDIR /opt/pfplay
#COPY . /opt/pfplay
#RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build -x test --no-daemon
##RUN ./gradlew clean build -x test --refresh-dependencies
#RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)
#
#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /opt/api
#ARG JAR_FILE=/opt/pfplay/build/libs/*.jar
#ENV JAR_NAME=pfplay-api-v1.jar
#COPY --from=build ${JAR_FILE} ${JAR_NAME}
#ENTRYPOINT ["sh", "-c", "java -jar ${JAR_NAME}"]
FROM eclipse-temurin:17-jdk-alpine as build

ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
RUN echo ${SPRING_PROFILES_ACTIVE}

WORKDIR /opt/pfplay
COPY . .
RUN ./gradlew clean build -x test --refresh-dependencies
#ENTRYPOINT ["sh", "-c", "java -jar ./build/libs/*-SNAPSHOT.jar"]
ENTRYPOINT ["sh", "-c", "java -jar -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE ./build/libs/*-SNAPSHOT.jar"]

