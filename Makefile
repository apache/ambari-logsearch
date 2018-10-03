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

package:
	$(MAVEN_BINARY) clean package

install:
	$(MAVEN_BINARY) clean install -DskipTests

be:
	$(MAVEN_BINARY) clean package -Pbe

fe:
	$(MAVEN_BINARY) clean package -Pfe

test:
	$(MAVEN_BINARY) clean test

rpm:
	$(MAVEN_BINARY) clean package -Dbuild-rpm -DskipTests

deb:
	$(MAVEN_BINARY) clean package -Dbuild-deb -DskipTests

package-jdk8:
	$(MAVEN_BINARY) clean package -Djdk.version=1.8

install-jdk8:
	$(MAVEN_BINARY) clean install -DskipTests -Djdk.version=1.8

be-jdk8:
	$(MAVEN_BINARY) clean package -Pbe -Djdk.version=1.8

fe-jdk8:
	$(MAVEN_BINARY) clean package -Pfe -Djdk.version=1.8

test-jdk8:
	$(MAVEN_BINARY) clean test -Djdk.version=1.8

rpm-jdk8:
	$(MAVEN_BINARY) clean package -Dbuild-rpm -DskipTests -Djdk.version=1.8

deb-jdk8:
	$(MAVEN_BINARY) clean package -Dbuild-deb -DskipTests -Djdk.version=1.8

docker-build:
	$(MAVEN_BINARY) clean package docker:build -DskipTests -Dlogsearch.docker.tag=$(LOGSEARCH_BUILD_DOCKER_TAG)

docker-push:
	$(MAVEN_BINARY) clean package docker:build docker:push -DskipTests -Dlogsearch.docker.tag=$(LOGSEARCH_BUILD_DOCKER_TAG)
