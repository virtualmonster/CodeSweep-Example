# syntax=docker/dockerfile:experimental
ARG MODE=dev
ARG MAVEN_IMAGE="maven:3-jdk-8-slim"
ARG INCLUDED_JDK="tp-docker-dev-local.us-artifactory1.nonprod.hclpnp.com/hcl-onetest/base-minimal-jdk8:10.2.1"

FROM $MAVEN_IMAGE AS builder
ARG VENDOR=HCL


# dev MODE
FROM builder AS dev-builder
ARG GOAL='verify'

# client MODE
FROM builder AS client-builder
ARG GOAL="flatten:flatten deploy -s settings.xml"


# prod MODE
FROM builder AS prod-builder
ARG GOAL="verify sonar:sonar"
ARG SONAR_LOGIN
ARG SONAR_HOST_URL
ARG BRANCH_NAME
ARG CACERTS_PATH="jre/lib/security/"

ARG SONAR_ARGS="-Dsonar.projectName=onetest-codesweep-extensibility -Dsonar.projectKey=com.hcl.onetest.codesweep.extensibility:codesweep-extensibility -Dsonar.branch.name=$BRANCH_NAME -Dsonar.login=$SONAR_LOGIN -Dsonar.host.url=$SONAR_HOST_URL"

RUN openssl s_client -connect tp-sonar1.nonprod.hclpnp.com:443 </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > /tmp/sonarqube.crt \
  && $JAVA_HOME/bin/keytool -import -noprompt -alias sonarqube -file /tmp/sonarqube.crt -keystore $JAVA_HOME/$CACERTS_PATH/cacerts -storepass changeit \
  && rm /tmp/sonarqube.crt


# all MODEs
FROM ${MODE}-builder as base
ARG GOAL
ARG MODE
RUN echo "using GOAL of $GOAL in $MODE-builder"

WORKDIR /usr/src/app
COPY . .

RUN echo "Running : mvn --batch-mode -f /usr/src/app/pom.xml $SONAR_ARGS clean $GOAL"

RUN --mount=id=maven_HCL,sharing=private,type=cache,target=/root/.m2 \
  mvn --batch-mode -f /usr/src/app/pom.xml $SONAR_ARGS clean $GOAL



FROM ${INCLUDED_JDK}
LABEL maintainer="HCL Software" \
      vendor="HCL Software" \
      description="HCL OneTest Server" \
      summary="HCL OneTest Server" \
      name="HCL OneTest Server"

ARG MAVEN_VERSION=3.6.3
ARG SHA=c35a1803a6e70a126e80b2b3ae33eed961f83ed74d18fcd16909b2d44d7dada3203f1ffe726c17ef8dcca2dcaa9fca676987befeadc9b9f759967a8cb77181c0
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries


#Install all needed tools
RUN	microdnf install -y nmap \
    && mkdir -p /usr/jdks \
    && mkdir -p /.m2 \
    && mkdir -p /usr/cache \
  	&& mkdir -p /usr/share/maven \
  	&& mkdir -p /usr/share/codesweep \
    && mkdir -p /usr/share/maven/ref \
    && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
    && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
    && rm -f /tmp/apache-maven.tar.gz \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV CODESWEEP_HOME /usr/share/codesweep
ENV MAVEN_HOME /usr/share/maven
ENV CACHE /usr/cache
ENV PATH_INIT $MAVEN_HOME:$CACHE
ENV PATH $JAVA_HOME/bin:$MAVEN_HOME:$CACHE:$PATH

RUN mkdir -p $HOME/codesweep-reports

COPY entrypointjava.sh /
COPY license*.txt /licenses/
COPY *.swidtag /swtags/

COPY --from=base /usr/src/app/codesweep-extensibility-docker-starter/target/codesweep-extensibility.docker.starter*.jar $CODESWEEP_HOME/codesweep-extensibility.docker.starter.jar
COPY --from=base /usr/src/app/codesweep-extensibility-docker-starter/codesweeplib/custom-scanners-service-1.0.10.jar $CODESWEEP_HOME/custom-scanners-service-1.0.10.jar
COPY --from=base /usr/src/app/codesweep-extensibility-docker-starter/codesweeplib/codesweep-report.html $CODESWEEP_HOME/codesweep-report.html

RUN chmod +x entrypointjava.sh \
  && chmod -R 777 $MAVEN_HOME \
  && chmod -R 777 $CODESWEEP_HOME \
  && chmod -R 777 $CACHE \
  && chmod -R 777 /home \
  && chmod -R 777 /.m2 \
  && chmod -R 777 $JAVA_HOME

WORKDIR	${CODESWEEP_HOME}
ENTRYPOINT ["/entrypointjava.sh"]
