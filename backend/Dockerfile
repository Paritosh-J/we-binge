# builder stage
FROM maven:3.9.3-eclipse-temurin-17 as builder
WORKDIR /app

# copy only what is needed to leverage Docker cache
COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

# runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/target/we-binge-backend-0.1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
