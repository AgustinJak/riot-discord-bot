FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -q -DskipTests

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /app/target/piumbotJ-1.0-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
