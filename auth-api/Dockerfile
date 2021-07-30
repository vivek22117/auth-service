FROM openjdk:8u212-jre-alpine

RUN mkdir /app
RUN apk --update add curl
HEALTHCHECK CMD curl -v --fail http://localhost:9004/internal/health || exit 1

COPY target/service.jar /app/service.jar
EXPOSE 9004
ENV ENVIRONMENT="qa"

ENTRYPOINT  java -Djava.net.preferIPv4Stack=true -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -jar /app/service.jar --spring.profiles.active=$ENVIRONMENT