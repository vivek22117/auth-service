FROM openjdk:8-jdk-alpine

RUN mkdir /app
RUN apk --update add curl
HEALTHCHECK CMD curl -v --fail http://localhost:8001/actuator/info || exit 1

COPY build/libs/service.jar /app/service.jar
EXPOSE 9002 8002
ENV ENVIRONMENT="local"

ENTRYPOINT  java -Djava.net.preferIPv4Stack=true -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -jar /app/service.jar