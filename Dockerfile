# ---------- Stage 1: build the .jar with Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copy pom.xml first so Maven downloads libraries only when pom.xml changes (faster rebuilds)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Stage 2: run the .jar on a small Java image ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# "prod" profile = settings in application-prod.properties
ENV SPRING_PROFILES_ACTIVE=prod
# Keep Java's memory inside the free plan's 512 MB limit
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
