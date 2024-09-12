FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/token-service-0.0.1-SNAPSHOT.war token-service.war
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "token-service.war"]
