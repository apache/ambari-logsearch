<!---
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements. See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

## Developement guide

## Requirements

- JDK 8 (JDK 11 is recommended) 
- Maven 3.5.x

## Backend development

### Prerequisites

- Install [docker](https://docs.docker.com/)
- For Mac OS X use [Docker for Mac](https://docs.docker.com/docker-for-mac/)
- [Docker compose](https://docs.docker.com/compose/) is also required.

### Build and start Log Search in docker container
```bash
# to see available commands: run start-logsearch without arguments
cd docker
./logsearch-docker build-and-run # build mvn project locally, build docker image, start containers
```
If you run the script at first time, it will generate you a new `Profile` file or an `.env` file inside docker directory (run twice if both missing and you want to generate Profile and .env as well), in .env file you should set `MAVEN_REPOSITORY_LOCATION` (point to local maven repository location, it uses `~/.m2` by default). These will be used as volumes for the docker container. Profile file holds the environment variables that are used inside the containers, the .env file is used outside of the containers

Then you can use the `logsearch-docker` script to start the containers (`start` command).
Also you can use docker-compose manually to start/manage the containers.
```bash
docker-compose up -d
```
After the logsearch container is started you can enter to it with following commands:
```bash
docker exec -it docker_logsearch_1 bash
```
In case if you started the containers separately and if you would like to access Solr locally with through your external ZooKeeper container, then point `solr` to `localhost` in your `/etc/hosts` file.

### Enable debug for Log Search Server or Log Feeder

By default remote debug is enabled in docker containers for Log Search server and Log Feeder (in order to use IDEs for debugging). Log Search server port for debug is `5005` and `5006` for Log Feeder. 

To suspend components in debug mode, you need to edit your `Profile` file (that was generated in docker folder) and set `LOGSEARCH_DEBUG_SUSPEND` or `LOGFEEDER_DEBUG_SUSPEND` to `true`.

### Run applications from IDE / maven

#### Start Log Search server locally from maven / IDE

Other services (like zookeeper, solr, logfeeder) can be started with `docker-compose`
```bash
cd ambari/ambari-logsearch/docker
docker-compose up -d zookeeper solr logfeeder
```

Then you can start Log Search server from maven 

```bash
cd ambari/ambari-logsearch/ambari-logsearch-server
./run.sh
# or
mvn clean package -DskipTests spring-boot:run
```

You can also start Log Search server from an IDE as well. One thing is important: the config set location that the server tries to upload to ZooKeeper. By default config sets are located at `${LOGSEARCH_SERVER_RELATIVE_LOCATION:}src/main/configsets` in `logsearch.properties`. Based or from where you run `LogSearch.java`, you need to set `LOGSEARCH_SERVER_RELATIVE_LOCATION` env variable properly. (or just simply use the ambari-logsearch-server as the working directory)

#### Start Log Feeder locally from maven / IDE

First you need to start every required service (except logfeeder), go to `ambari-logsearch/docker` folder and run:
```bash
docker-compose up -d zookeeper solr logsearch
```

Secondly, if you are planning to run Log Feeder from an IDE, for running the LogFeeder main methoud, you will need to set the working directory to `ambari/ambari-logsearch/ambari-logsearch-logfeeder` or set `LOGFEEDER_RELATIVE_LOCATION` env variable.
With Maven, you won't need these steps, just run this command from the ambari-logsearch-logfeeder folder:

```bash
mvn clean package -DskipTests spring-boot:run
```

For Log Feeder, it is also important to use the ambari-logsearch-logfeeder as a working directory if you are trying to run the application from an IDE.

### Package build process

1. Check out the code from GIT repository

2. On the logsearch root folder (ambari/ambari-logsearch), please execute the following make command to build RPM/DPKG:
```bash
make rpm
# or for jdk11
export LOGSEARCH_JDK_11=true
make rpm
```
  or
```bash
make deb
# or for jdk11
export LOGSEARCH_JDK_11=true
make deb
```
3. Generated RPM/DPKG files will be found in ambari-logsearch-assembly/target folder

### Running Integration Tests

By default integration tests are not a part of the build process, you need to set -Dbackend-tests or -Dselenium-tests (or you can use -Dall-tests to run both). To running the tests you will need docker here as well (right now docker-for-mac and unix are supported by default, for boot2docker you need to pass -Ddocker.host parameter to the build).

```bash
# from ambari-logsearch folder
mvn clean integration-test -Dbackend-tests failsafe:verify
# or run selenium tests with docker for mac, but before that you nedd to start xquartz
open -a XQuartz
# then in an another window you can start ui tests
mvn clean integration-test -Dselenium-tests failsafe:verify
# you can specify story file folde location with -Dbackend.stories.location and -Dui.stories.location (absolute file path) in the commands
```
Also you can run from the IDE, but make sure all of the ambari logsearch modules are built.


### Update version (for release or specific builds)

```bash
make update-version new-version="2.8.0.0-11"
```

### UI development

TODO