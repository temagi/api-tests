FROM openjdk:12-jdk-alpine

WORKDIR .

COPY . .

#ENTRYPOINT exec java -jar api-tests.jar
ENTRYPOINT exec ./gradlew clean build