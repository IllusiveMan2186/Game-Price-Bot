# Stage 1: Build the application using Gradle
FROM gradle:8.0.2 AS builder
ENV APP_HOME=/gpb/email/
WORKDIR $APP_HOME

# Build args for private repo
ARG DEPENDENCY_REPO_URL
ARG DEPENDENCY_REPO_USERNAME
ARG DEPENDENCY_REPO_PASSWORD
ENV DEPENDENCY_REPO_URL=${DEPENDENCY_REPO_URL} \
    DEPENDENCY_REPO_USERNAME=${DEPENDENCY_REPO_USERNAME} \
    DEPENDENCY_REPO_PASSWORD=${DEPENDENCY_REPO_PASSWORD}

# Cache dependencies and build
COPY settings.gradle build.gradle gradle/ ./
RUN gradle --no-daemon dependencies
COPY . .
RUN gradle --no-daemon clean build


# Stage 2: Prepare lightweight runtime image
FROM eclipse-temurin:17-jre-jammy
LABEL org.opencontainers.image.source="https://github.com/IllusiveMan2186/Game-Price-Bot/tree/main/gpb-email"
ENV APP_HOME=/gpb/email/
WORKDIR $APP_HOME

COPY --from=builder /gpb/email/build/libs/*.jar ./

RUN JAR_FILE=$(find . -maxdepth 1 -name "*.jar" ! -name "*plain*" | head -n 1) \
    && mv "$JAR_FILE" gpb-email.jar

# Run as non-root user
USER 1000:1000

ENTRYPOINT ["java", "-jar", "./gpb-email.jar"]