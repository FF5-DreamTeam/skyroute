FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
# Optional: set non-root user
RUN useradd -ms /bin/bash appuser
USER appuser
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseZGC"
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_ADDRESS=0.0.0.0

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.address=0.0.0.0 -jar app.jar"]
