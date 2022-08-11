FROM openjdk:18-slim

WORKDIR /app

COPY ./target/regionalweather-0.0.1-SNAPSHOT.jar .

ENTRYPOINT [ "java","-jar", "regionalweather-0.0.1-SNAPSHOT.jar" ]