FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /code

COPY . .
RUN \
    --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,target=/root/.m2/settings.xml,id=mvn-settings,required=true \
    mvn package -DskipTests

FROM eclipse-temurin:21-jre

COPY --from=build /code/application/target/*.jar /application.jar
ENTRYPOINT [ "java", "-jar", "/application.jar" ]
