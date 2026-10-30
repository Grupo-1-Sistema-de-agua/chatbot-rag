# syntax=docker/dockerfile:1

# Etapa 1: compilación con Maven y JDK 17
FROM maven:3.9-eclipse-temurin-17 AS construccion
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q package -DskipTests

# Etapa 2: ejecución con JRE 17
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system chatbot \
    && useradd --system --gid chatbot chatbot
COPY --from=construccion /app/target/*.jar app.jar
USER chatbot
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]