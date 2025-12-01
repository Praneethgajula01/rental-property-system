# Use an official Java runtime as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy your built jar file into the container
COPY target/*.jar app.jar

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
