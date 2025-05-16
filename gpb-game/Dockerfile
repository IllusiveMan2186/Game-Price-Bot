# ───────────────── Stage 1: Build the app ─────────────────
FROM gradle:8.0.2 AS build
ENV APP_HOME=/gpb/game/
WORKDIR $APP_HOME

# Build args for private dependency repo
ARG DEPENDENCY_REPO_URL
ARG DEPENDENCY_REPO_USERNAME
ARG DEPENDENCY_REPO_PASSWORD
ENV DEPENDENCY_REPO_URL=${DEPENDENCY_REPO_URL} \
    DEPENDENCY_REPO_USERNAME=${DEPENDENCY_REPO_USERNAME} \
    DEPENDENCY_REPO_PASSWORD=${DEPENDENCY_REPO_PASSWORD}

# Cache Kotlin-DSL build scripts and download dependencies
COPY build.gradle.kts settings.gradle.kts gradle/ ./
RUN gradle --no-daemon dependencies

# Copy source & compile
COPY . .
RUN gradle --no-daemon clean build


# ───────────────── Stage 2: Final runtime ─────────────────
FROM eclipse-temurin:17-jre-jammy
LABEL org.opencontainers.image.source="https://github.com/IllusiveMan2186/Game-Price-Bot/tree/main/gpb-game"
ENV APP_HOME=/gpb/game/
WORKDIR $APP_HOME

# Copy built JAR from build stage
COPY --from=build $APP_HOME/build/libs/*.jar gpb-game.jar

# Copy default image resource into container
COPY src/main/resources/defaultImage.png /image_folder/defaultImage.png

# Ensure the Lucene index directory exists and is writable by non-root
RUN mkdir -p ${APP_HOME}/Game \
    && chown -R 1000:1000 ${APP_HOME}

# Drop privileges to non-root user
RUN chown -R 1000:1000 /image_folder
USER 1000:1000

# Launch the application
ENTRYPOINT ["java", "-jar", "./gpb-game.jar"]