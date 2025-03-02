# Stage 1: Build the application with Maven
FROM maven:3.8.4 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/gpb/telegram/
WORKDIR $APP_HOME

# Copy the Maven configuration files
COPY pom.xml $APP_HOME

# Copy the source code
COPY src $APP_HOME/src

ARG DEPENDENCY_REPO_URL
ARG DEPENDENCY_REPO_USERNAME
ARG DEPENDENCY_REPO_PASSWORD
ENV DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL
ENV DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME
ENV DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD

COPY settings.xml /root/.m2/settings.xml

RUN sed -i 's|${env.DEPENDENCY_REPO_USERNAME}|'"$DEPENDENCY_REPO_USERNAME"'|g' /root/.m2/settings.xml \
    && sed -i 's|${env.DEPENDENCY_REPO_PASSWORD}|'"$DEPENDENCY_REPO_PASSWORD"'|g' /root/.m2/settings.xml

RUN cat /root/.m2/settings.xml

# Build the application
RUN mvn clean package

# Stage 2: Create the final image
FROM openjdk:17
ENV ARTIFACT_NAME=gpb-telegram-1.1.1.jar
ENV APP_HOME=/gpb/telegram/

WORKDIR $APP_HOME

# Copy the built JAR file from the temporary build image
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/target/$ARTIFACT_NAME .

# Define the command to run on container start
CMD java -jar $ARTIFACT_NAME