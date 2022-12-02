# Start with a base image containing Java runtime
FROM adoptopenjdk/openjdk11:alpine-jre

# Add Maintainer Info
LABEL maintainer="sean.lihai@gmail.com" 

# Add a volume pointing to /tmp
VOLUME /tmp

# The application's jar file
ARG JAR_FILE=target/*.jar

# Add the application's jar to the container
COPY target/*.jar /opt/app/service.jar

WORKDIR /opt/app

# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar service.jar"]

