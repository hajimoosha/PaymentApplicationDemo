# Build stage
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -DskipTests package -q

# Run stage
FROM eclipse-temurin:17-jre
ARG JAR_FILE=payments-service/target/payments-service-0.0.1-SNAPSHOT.jar
COPY --from=build /workspace/payments-service/target/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
