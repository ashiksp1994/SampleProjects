FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/user-service-0.0.1-SNAPSHOT.war user-service.war
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "user-service.war"]
