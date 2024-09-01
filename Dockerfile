FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY target/bug-api-1.0.0.jar target/

WORKDIR /app/target
ENTRYPOINT java -jar bug-api-1.0.0.jar