# Stage 1: Build the application
FROM gradle:8.0.2 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/gpb/backend/
WORKDIR $APP_HOME
COPY build.gradle.kts settings.gradle.kts $APP_HOME

COPY . .
RUN gradle clean build

# Stage 2: Create the final image
FROM openjdk:20
ENV ARTIFACT_NAME=gpb-backend-0.0.1-SNAPSHOT.war
ENV APP_HOME=/gpb/backend/

WORKDIR $APP_HOME

# Copy the built WAR file from the temporary build image
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .

# Define the command to run on container start
CMD java -jar $ARTIFACT_NAME