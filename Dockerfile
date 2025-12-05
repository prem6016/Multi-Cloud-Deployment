# Stage 1: build with Maven
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy only what is needed to leverage Docker cache
COPY pom.xml .
# copy mvnw and .mvn if present (optional)
# COPY .mvn .mvn
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

# Stage 2: runtime image
FROM eclipse-temurin:17-jdk-jammy
ARG JAR_FILE=/workspace/target/*.jar
COPY --from=builder ${JAR_FILE} /app/app.jar

# (Optional) set timezone or JVM opts via env
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
