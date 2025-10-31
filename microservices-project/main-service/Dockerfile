ARG MODULE_NAME=main-service

# --- BUILD STAGE ---

FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /microservices-project

ARG MODULE_NAME

COPY pom.xml .
COPY ${MODULE_NAME}/pom.xml ./${MODULE_NAME}/pom.xml
COPY ${MODULE_NAME}/src ./${MODULE_NAME}/src

RUN mvn -f ${MODULE_NAME}/pom.xml clean package -DskipTests

# --- RUNTIME STAGE ---

FROM eclipse-temurin:23-jre
WORKDIR /microservices-project

ARG MODULE_NAME
ENV APP_NAME=main-service
ENV SPRING_PROFILES_ACTIVE=docker
ARG VERSION=1.0-SNAPSHOT

COPY --from=build /microservices-project/${MODULE_NAME}/target/${APP_NAME}-${VERSION}.jar ${APP_NAME}.jar
ENTRYPOINT ["sh", "-c", "java -jar /microservices-project/${APP_NAME}.jar"]