# ──────────────────────────────────────────────
# Stage 1: Build the app (e.g., Gradle, npm, etc.)
# ──────────────────────────────────────────────
FROM gradle:8.0.2 AS build
WORKDIR /app
COPY . .
ARG DEPENDENCY_REPO_URL
ARG DEPENDENCY_REPO_USERNAME
ARG DEPENDENCY_REPO_PASSWORD
ENV DEPENDENCY_REPO_URL=$DEPENDENCY_REPO_URL
ENV DEPENDENCY_REPO_USERNAME=$DEPENDENCY_REPO_USERNAME
ENV DEPENDENCY_REPO_PASSWORD=$DEPENDENCY_REPO_PASSWORD
RUN gradle clean build

# ──────────────────────────────────────────────
# Stage 2: Final runtime
# ──────────────────────────────────────────────
FROM openjdk:17-jdk-alpine
ENV APP_HOME=/app
WORKDIR $APP_HOME

# Copy the built artifact from build stage
COPY --from=build /$APP_HOME/build/libs/*.war app.war

# Run the app
CMD ["java", "-jar", "app.war"]
