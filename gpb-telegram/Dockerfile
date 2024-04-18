# Stage 1: Build the application with Maven
FROM maven:3.8.4 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/gpb/telegram/
WORKDIR $APP_HOME

# Copy the Maven configuration files
COPY pom.xml $APP_HOME

# Copy the source code
COPY src $APP_HOME/src

# Build the application
RUN mvn clean package

# Stage 2: Create the final image
FROM openjdk:20
ENV ARTIFACT_NAME=gpb-telegram-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/gpb/telegram/

WORKDIR $APP_HOME

# Copy the built JAR file from the temporary build image
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/target/$ARTIFACT_NAME .

# Define the command to run on container start
CMD java -jar $ARTIFACT_NAME