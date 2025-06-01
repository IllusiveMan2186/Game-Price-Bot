# ───────────────── Stage 1: Build the app ─────────────────
FROM maven:3.8.4 AS build
ENV APP_HOME=/gpb/telegram/
WORKDIR $APP_HOME

# Build args for private repo
ARG DEPENDENCY_REPO_URL
ARG DEPENDENCY_REPO_USERNAME
ARG DEPENDENCY_REPO_PASSWORD
ENV DEPENDENCY_REPO_URL=${DEPENDENCY_REPO_URL} \
    DEPENDENCY_REPO_USERNAME=${DEPENDENCY_REPO_USERNAME} \
    DEPENDENCY_REPO_PASSWORD=${DEPENDENCY_REPO_PASSWORD}

# Copy Maven project file
COPY pom.xml ./

# Copy and configure settings for private repo authentication
COPY settings.xml /root/.m2/settings.xml
RUN sed -i 's|${env.DEPENDENCY_REPO_USERNAME}|${DEPENDENCY_REPO_USERNAME}|g' /root/.m2/settings.xml \
 && sed -i 's|${env.DEPENDENCY_REPO_PASSWORD}|${DEPENDENCY_REPO_PASSWORD}|g' /root/.m2/settings.xml

# Fetch all dependencies
RUN mvn --batch-mode dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn --batch-mode clean package


# ───────────────── Stage 2: Final runtime ─────────────────
FROM eclipse-temurin:17-jre-jammy
LABEL org.opencontainers.image.source="https://github.com/IllusiveMan2186/Game-Price-Bot/tree/main/gpb-telegram"
ENV ARTIFACT_NAME=gpb-telegram.jar
ENV APP_HOME=/gpb/telegram/
WORKDIR $APP_HOME

# Copy built JAR from build stage
COPY --from=build $APP_HOME/target/*.jar ${ARTIFACT_NAME}

# Launch the application
ENTRYPOINT ["java", "-jar", "./gpb-telegram.jar"]
