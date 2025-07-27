# Use Java 17 base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Give execution permission to mvnw
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Package and build
RUN ./mvnw package -DskipTests

# Run the JAR
ENTRYPOINT ["java", "-jar", "target/hair-salon-backend-0.0.1-SNAPSHOT.jar"]
