# Build stage
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Package stage
FROM eclipse-temurin:17-jre
WORKDIR /app
# Usando curinga para encontrar o JAR independente do nome exato
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
