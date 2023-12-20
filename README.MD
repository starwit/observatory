# Data Backend
This repository contains a software module written in Java, that can run analytical jobs, to condense raw tracking data from Starwit's Awareness Engine (SAE) into usable knowledge. Software is targeted to be run in a Kubernetes cluster. Thus this repo produces a [Docker image](https://hub.docker.com/repository/docker/starwitorg/databackend/general) and DataBackend can be deployed using Helm.

Its current application focus is to extract usable knowledge about traffic from raw data produced by SAE.

## License & Usage
Project is licensed under AGPL 3 and the license can be found [here](LICENSE). This component is part of a publicly funded project by the city of Wolfsburg and thus usage in your community is very much encouraged. It is part of a group of software modules that shall help communities to manage traffic and to gain statistical insights. 

For mor information how complete software stack can be used and what it can do for your community please refer to the main repository at: https://gitlab.opencode.de/OC000026793282/testfeld-smart-parking-wolfsburg 

More details on political and organizational background can be found here: https://www.wolfsburg.de/en-us/leben/smart-city

### Contribution
TODO

## Analytical Jobs
Main task of DataBackend is to run a multitude of analytical jobs that turn raw tracking data into usable knowledge. This section thus lists all available jobs and a brief description of their intended function.

### Counting Traffic
This job counts traffic passing a line including direction of movement.

### Counting Parked Vehicles
In a defined area this job counts how many vehicles are not moving and so considered as parked vehicles.

## How to Deploy/Install
Helm is the preferred tool to install DataBackend. Installation can be done with the following command:

```bash
helm -n yournamespace install databackend oci://registry-1.docker.io/starwitorg/databackend -f yourvalues.yaml
```

Please note, that namespace is optional and you can define your own release name. For how to use Helm refer to their [docs](https://helm.sh/docs/intro/using_helm/).

More details on the values you need to provide in order run Helm chart on your environment can be found [here](deployment/helm/databackend/Readme.md).

Once you have installed DataBackend you can reach it's API documentation at http://domain/databackend/swagger-ui/index.html.

Please note DataBackend is just one of a collection of components and thus helmfile can also be used to install DataBackend along side everything else. This is described here: https://gitlab.opencode.de/OC000026793282/testfeld-smart-parking-wolfsburg/-/tree/main/deployment?ref_type=heads

## How to Build

__Prerequisites__ 

* Java JDK 17 or later
* Maven 3
* PostgreSQL (available for development via docker-compose scripts)
* Keycloak for authentication (optional)

This software is written in Java and dependency/build is managed by Maven. And so building the software is straight forward, just run the following command in repo's root folder:

```bash
mvn clean install
```

After running Maven you can find a jar-file in folder _application/target_ and that can be run like so:

```bash
java -jar application/target/application-0.0.1-SNAPSHOT.jar
```

Note that application will need certain databases to run properly. You can either setup all databases manually or you can use Docker compose. To do that go to folder _deployment_ and start the environment (databases and keycloak) via docker-compose:

```bash
cd deployment
docker-compose -f localenv-docker-compose.yml up
```

When using Docker compose application can be reached via those coordinates:

* main context http://localhost:8081/databackend/
* API docs / Swagger UI: http://localhost:8081/databackend/swagger-ui/index.html
* If you are using keycloak:
    * default user/password is admin/admin
    * keycloak can be reached under http://localost:8080/auth

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
