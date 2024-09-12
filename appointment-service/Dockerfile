FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/appointment-service-0.0.1-SNAPSHOT.war appointment-service.war
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "appointment-service.war"]
