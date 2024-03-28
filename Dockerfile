FROM openjdk:21-ea-oracle
MAINTAINER tecklens.com
COPY build/libs/spl-keycloak-0.0.1.jar spl-keycloak-0.0.1.jar
ENTRYPOINT ["java","-jar","/spl-keycloak-0.0.1.jar", "-Xmx512m"]