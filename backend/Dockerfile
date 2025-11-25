# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copy only the pom.xml to leverage Docker cache
COPY pom.xml .

# 2. Download dependencies into a separate layer
# This layer is only invalidated if pom.xml changes
RUN mvn dependency:go-offline

# 3. Copy the rest of the source code
COPY src ./src

# 4. Build the application using the cached dependencies
RUN mvn clean package -DskipTests

# Package stage
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/chungtayphongngua-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
