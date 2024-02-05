
FROM gradle:8.0.2 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/gpb/email/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle

COPY . .
RUN gradle clean build

FROM openjdk:20
ENV ARTIFACT_NAME=gpb-email-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/gpb/email/

WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .

ENTRYPOINT exec java -jar ${ARTIFACT_NAME}