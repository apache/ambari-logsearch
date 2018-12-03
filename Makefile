# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

GIT_REV_SHORT = $(shell git rev-parse --short HEAD)
MAVEN_BINARY ?= mvn

ifeq ("$(USE_GIT_BUILD_TAG)", "true")
  LOGSEARCH_BUILD_DOCKER_TAG = build-$(GIT_REV_SHORT)
else
  LOGSEARCH_BUILD_DOCKER_TAG = "latest"
endif

ifeq ("$(LOGSEARCH_JDK_11)", "true")
  LOGSEARCH_JAVA_VERSION = "11"
else
  LOGSEARCH_JAVA_VERSION = "1.8"
endif

package:
	$(MAVEN_BINARY) clean package -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

install:
	$(MAVEN_BINARY) clean install -DskipTests -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

be:
	$(MAVEN_BINARY) clean package -Pbe -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

fe:
	$(MAVEN_BINARY) clean package -Pfe -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

test:
	$(MAVEN_BINARY) clean test -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

rpm:
	$(MAVEN_BINARY) clean package -Dbuild-rpm -DskipTests -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

deb:
	$(MAVEN_BINARY) clean package -Dbuild-deb -DskipTests -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

prop-docs:
	$(MAVEN_BINARY) -pl ambari-logsearch-docs clean package exec:java -DskipTests -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

update-version:
	$(MAVEN_BINARY) versions:set-property -Dproperty=revision -DnewVersion=$(new-version) -DgenerateBackupPoms=false

docker-build:
	$(MAVEN_BINARY) clean package docker:build -DskipTests -Dlogsearch.docker.tag=$(LOGSEARCH_BUILD_DOCKER_TAG) -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

docker-push:
	$(MAVEN_BINARY) clean package docker:build docker:push -DskipTests -Dlogsearch.docker.tag=$(LOGSEARCH_BUILD_DOCKER_TAG) -Djdk.version=$(LOGSEARCH_JAVA_VERSION)

docker-dev-start:
	cd docker && docker-compose up -d

docker-dev-build-and-start:
	$(MAVEN_BINARY) clean package && cd docker && docker-compose up -d
