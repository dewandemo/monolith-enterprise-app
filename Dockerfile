# syntax=docker/dockerfile:1
#
# Snowman - Enterprise Application
#
# Multi-stage build. Note: the application targets Java 7 bytecode (see pom.xml
# <java.version>) but is compiled/run on JDK 8 since JDK 7 is no longer
# available on current base images - a common situation for legacy enterprise
# codebases that have fallen behind on their runtime but not yet modernized
# the actual source/build config.

FROM maven:3.6-jdk-8 AS build
WORKDIR /build
COPY pom.xml .
# Pre-fetch dependencies in their own layer for caching.
RUN mvn -B -q dependency:go-offline -Dliquibase.should.run=false || true
COPY src ./src
RUN mvn -B -q clean package -Dliquibase.should.run=false -DskipTests

FROM eclipse-temurin:8-jre
LABEL maintainer="Snowman Enterprise App"
WORKDIR /app

COPY --from=build /build/target/Snowman.jar ./Snowman.jar

ENV JAVA_OPTS="-Xms256m -Xmx1024m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar Snowman.jar"]
