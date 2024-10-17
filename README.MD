# Observatory

This repository contains a software module written in Java, that can run analytical jobs, to condense raw tracking data from Starwit's Awareness Engine (SAE) into usable knowledge. Software is targeted to be run in a Kubernetes cluster. Thus this repo produces a [Docker image](https://hub.docker.com/repository/docker/starwitorg/observatory/general) and Observatory can be deployed using Helm.

Its current application focus is to extract usable knowledge about traffic from raw data produced by SAE.

## License & Usage

Project is licensed under AGPL 3 and the license can be found [here](LICENSE). This component is part of a publicly funded project by the city of Wolfsburg and thus usage in your community is very much encouraged. It is part of a group of software modules that shall help communities to manage traffic and to gain statistical insights. 

For mor information how complete software stack can be used and what it can do for your community please refer to the main repository at: https://gitlab.opencode.de/OC000026793282/testfeld-smart-parking-wolfsburg 

More details on political and organizational background can be found here: https://www.wolfsburg.de/en-us/leben/smart-city

### Contribution

We are grateful for any contribution. Please refer to our [contribution guideline](CONTRIBUTING.md) and instructions document for any information.

## Observation Jobs

Main task of Observatory is to run a multitude of observation jobs that turn raw tracking data into usable knowledge. This section thus lists all available jobs and a brief description of their intended function.

### Counting Traffic

This job counts traffic passing a line including direction of movement. The technical name is `LINE_CROSSING`.

### Counting Parked Vehicles

In a defined area this job counts how many vehicles are not moving and so considered as parked vehicles. The technical name is `AREA_OCCUPANCY`.

## How to Deploy/Install

Helm is the preferred tool to install Observatory. Installation can be done with the following command:

```bash
helm -n yournamespace install observatory oci://registry-1.docker.io/starwitorg/observatory -f yourvalues.yaml
```

Please note, that namespace is optional and you can define your own release name. For how to use Helm refer to their [docs](https://helm.sh/docs/intro/using_helm/).

More details on the values you need to provide in order run Helm chart on your environment can be found [here](deployment/helm/observatory/Readme.md).

Once you have installed Observatory you can reach it's API documentation at http://domain/observatory/swagger-ui/index.html.

Please note Observatory is just one of a collection of components and thus helmfile can also be used to install Observatory along side everything else. This is described here: https://gitlab.opencode.de/OC000026793282/testfeld-smart-parking-wolfsburg/-/tree/main/deployment?ref_type=heads

## How to Build

__Prerequisites__ 

* Java JDK 21 or later
* Maven 3
* PostgreSQL (available for development via docker-compose scripts)
* Keycloak for authentication (optional)
* `vision-api` maven repository setup (see https://github.com/starwit/vision-api?tab=readme-ov-file#java--maven for guidance)

This software is written in Java and dependency/build is managed by Maven. And so building the software is straight forward.

Note that application will need certain databases to run properly. You can either setup all databases manually or you can use Docker compose. To do that go to folder _deployment_ and start the environment (databases and keycloak) via docker-compose:

```bash
cd deployment
docker compose up
```

Run following Command in Root folder:

```bash
mvn clean install
```

After running Maven you can find a jar-file in folder _application/target_ and that can be run like so:

```bash
java -jar application/target/application-*.jar
```

Alternatively, you can run the application using the Spring Boot Maven Plugin:

```bash
mvn spring-boot:run
```

When using Docker compose application the API can be reached at: http://localhost:8082/observatory/api/ (e.g. .../api/obvervationJob)

## How to (manually) test

* Start a SAE instance (see https://github.com/starwit/starwit-awareness-engine/tree/main/docker-compose)
* Start Observatory infrastructure (`./deployment/postgreslocal-docker-compose.yaml`)
* Start Observatory with default settings
* Add an observation job that makes sense for the video your SAE is processing (use `POST /api/observation-job`).\
  The easiest way is to use the integrated Swagger UI with the payload below (http://localhost:8082/observatory/swagger-ui/index.html).\
  Sample for the usual video:

    ```json
    {
        "name": "job1",
        "observationAreaId": 1,
        "cameraId": "stream1",
        "detectionClassId": 2,
        "type": "LINE_CROSSING",
        "enabled": true,
        "geoReferenced": false,
        "classification": "Lichtschranke",
        "geometryPoints": [
            {
                "x": 0.3096,
                "y": 0.609,
                "orderIdx": 0
            },
            {
                "x": 0.544,
                "y": 0.8097,
                "orderIdx": 1
            }
        ]
    }
    ```

## Debugging

You can start the spring boot application in debug mode. See Spring Boot documentation for further details. The easiest way is, to use debug functionality integrated with your IDE like VS Code.

## Postgres Client

The database is available under localhost:5434. A database GUI (pgadmin) is available at localhost:5050 (if you started one of the docker compose environments in `./deployment`).

```properties
Username:observatory
Database:observatory
Password:observatory
```
