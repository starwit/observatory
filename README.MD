# Starwit Data Backend
This repository contains a software module written in Java, that can run analytical jobs, to condense raw tracking data from Starwit's Awareness Engine into usable knowledge. Software is targeted to be run in a Kubernetes cluster. Thus this repo produces a [Docker image](https://hub.docker.com/repository/docker/starwitorg/databackend/general).

## License & Usage
Project is licensed under AGPL-3.0

## Analytical Jobs

## How to Build

## How to Deploy


### Prerequisites

* Java JDK 17 or later
* Maven 3
* Postgres (available for development via docker-compose scripts)
* using Keycloak is optional

### Installation Steps

:exclamation: Each step is executed from the project home directory.

1) go to the deployment folder and start the environment (database and keycloak) via docker-compose:

    ```bash
    cd deployment
    docker-compose -f localenv-docker-compose.yml up
    ```

2) build the project

    ```bash
    mvn clean install
    ```

4) start project

    ```bash
    java -jar application/target/application-0.0.1-SNAPSHOT.jar
    ```
   You can also run the main-class via Visual Studio Code.

* **databackend can be reached under http://localhost:8081/databackend/**
* **access to swagger-ui: http://localhost:8081/databackend/swagger-ui/index.html**
* **If you are using keycloak:**
    * **default user/password is admin/admin**
    * **keycloak can be reached under http://localost:8080/auth**

### Debugging

You can start the spring boot application in debug mode. See Spring Boot documentation for further details. The easiest way is, to use debug functionality integrated with your IDE like VS Code.

### Postgres Client

The database is available under localhost:54321

```
Username:databackend
Database:databackend
Password:databackend
```
PGAdmin is recommended to access database for development purpose. It can be deployed via docker-compose file.

### Starting without keycloak

If you want to start your application without keycloak, you need to change spring boot profile to something different than auth (e.g. dev) in application\src\main\resources\application.properties. Hence, the file application-auth.properties will be ignored.

```properties
spring.profiles.active=dev
```

or define env-variable

```bash
SPRING_PROFILES_ACTIVE=dev
```

Start the database without keycloak:

```bash
cd deployment
docker-compose -f postgreslocal-docker-compose.yml up
```
