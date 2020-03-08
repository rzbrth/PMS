# latest oracle openjdk is the basis
FROM openjdk:8

MAINTAINER Rajib Rath <rajibrath20@gmail.com>

RUN mvn clean install

# copy jar file into container image under app directory
EXPOSE 9090

# expose server port accept connections
ADD target/pms.jar pms.jar

# start application
ENTRYPOINT ["java", "-jar", "/pms.jar"]