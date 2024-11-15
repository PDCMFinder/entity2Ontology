# Use a base JDK image
FROM openjdk:22-jdk-slim

LABEL authors="mmartinez@ebi.ac.uk"

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR and dependency libraries
COPY target/entity2Ontology-1.0-SNAPSHOT.jar /app/
COPY target/lib/ /app/lib/

# Set the CLASSPATH to include the JAR file and all dependencies in the lib folder
ENV CLASSPATH "/app/entity2Ontology-1.0-SNAPSHOT.jar:/app/lib/*"

# Default ENTRYPOINT for running the JAR
ENTRYPOINT ["java", "-jar", "/app/entity2Ontology-1.0-SNAPSHOT.jar"]
