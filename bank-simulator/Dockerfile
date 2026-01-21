FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -DskipTests package -q

FROM eclipse-temurin:17-jre
COPY --from=build /workspace/bank-simulator/target/*.jar /app/bank.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/bank.jar"]
