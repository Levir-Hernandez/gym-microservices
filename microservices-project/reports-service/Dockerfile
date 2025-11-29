ARG MODULE_NAME=reports-service

# --- BUILD STAGE ---

FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /microservices-project

ARG MODULE_NAME

COPY pom.xml .
COPY ${MODULE_NAME}/pom.xml ./${MODULE_NAME}/pom.xml
COPY ${MODULE_NAME}/src ./${MODULE_NAME}/src

RUN mvn -f ${MODULE_NAME}/pom.xml clean package -DskipTests

# --- RUNTIME STAGE ---
FROM sapmachine:23-jre-ubuntu-jammy
RUN apt-get update && apt-get install -y libcurl4

WORKDIR /microservices-project

ARG MODULE_NAME
ARG VERSION=1.0-SNAPSHOT
ENV APP_NAME=reports-service
ENV SPRING_PROFILES_ACTIVE=standalone

COPY --from=build /microservices-project/${MODULE_NAME}/target/${APP_NAME}-${VERSION}.jar ${APP_NAME}.jar
ENTRYPOINT ["sh", "-c", "java -jar /microservices-project/${APP_NAME}.jar"]