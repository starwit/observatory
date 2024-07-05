FROM maven:3-eclipse-temurin-21 as build

WORKDIR /code

COPY ${HOME}/.m2/settings.xml /root/.m2

COPY pom.xml .
COPY application/pom.xml application/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY rest/pom.xml rest/pom.xml
COPY service/pom.xml service/pom.xml

RUN mvn verify --fail-never

COPY . .
RUN mvn package

FROM eclipse-temurin:21-jre

COPY --from=build /code/target/*.jar /application.jar
ENTRYPOINT [ "java", "-jar", "/application.jar" ]
