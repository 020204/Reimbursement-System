# Backend Production Dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy Maven settings with Aliyun mirror
COPY .mvn /app/.mvn
COPY mvnw /app/mvnw
COPY mvnw.cmd /app/mvnw.cmd

COPY pom.xml .
RUN mvn dependency:go-offline -B || true

COPY src ./src

RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=builder /app/target/reimbursement-system-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dspring.profiles.active=docker"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
