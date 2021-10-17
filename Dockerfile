FROM openjdk:11 as builder
WORKDIR application
COPY ./gradle gradle
COPY ./gradle.properties gradle.properties
COPY ./gradlew gradlew
COPY ./settings.gradle settings.gradle
COPY ./build.gradle build.gradle
COPY ./src src


RUN chmod +x ./gradlew
RUN ./gradlew build

FROM openjdk:11-jre

COPY --from=builder application/build/libs/*.jar site-api.jar

LABEL noughtcrosses=noughtcrosses:latest

EXPOSE 8484
ENTRYPOINT ["java", "-jar", "noughtcrosses.jar"]
