# ───────────────── Stage 1: Build the app ─────────────────
FROM gradle:8.0.2 AS build
ENV APP_HOME=/gpb/backend/
WORKDIR $APP_HOME

# Build args for private repo
ARG DEPENDENCY_REPO_URL
ARG DEPENDENCY_REPO_USERNAME
ARG DEPENDENCY_REPO_PASSWORD
ENV DEPENDENCY_REPO_URL=${DEPENDENCY_REPO_URL} \
    DEPENDENCY_REPO_USERNAME=${DEPENDENCY_REPO_USERNAME} \
    DEPENDENCY_REPO_PASSWORD=${DEPENDENCY_REPO_PASSWORD}

# Cache dependencies and build
COPY settings.gradle.kts build.gradle.kts gradle/ ./
RUN gradle --no-daemon dependencies
COPY . .
RUN gradle --no-daemon clean bootJar


# ───────────────── Stage 2: Final runtime ─────────────────
FROM eclipse-temurin:17-jre-jammy
LABEL org.opencontainers.image.source="https://github.com/IllusiveMan2186/Game-Price-Bot/tree/main/gpb-backend"
ENV APP_HOME=/gpb/backend/
WORKDIR $APP_HOME

COPY --from=build /gpb/backend/build/libs/*.jar ./

RUN JAR_FILE=$(find . -maxdepth 1 -name "*.jar" ! -name "*plain*" | head -n 1) \
    && mv "$JAR_FILE" gpb-backend.jar

# Run as non-root user
USER 1000:1000

ENTRYPOINT ["java", "-jar", "gpb-backend.jar"]