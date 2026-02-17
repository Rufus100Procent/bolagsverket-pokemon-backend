# Stage 1: Build with Maven + JDK 21
FROM maven:3-amazoncorretto-21 AS build
WORKDIR /app/myapp
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

# Stage 2: Package the application in a runtime image
FROM amazoncorretto:21-alpine
WORKDIR /app/myapp
ENV SPRING_PROFILES_ACTIVE=staging
COPY --from=build /app/myapp/target/ROOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
