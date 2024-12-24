# Email part
Common part of GPB application that responsible for common service and entities that needed to multiple services
Needed for avoiding code duplication and usage of entities and services without conflicts

### Setup for development environment:

* JDK 17 or higher
* Gradle 8.0.2 or higher

## Step for publishing new version of common services:
* WARNING: Change version if you changed services
* Setup dependency repository for common service
    * Create or add to gradle.properties at directory %LocalUser%/.gradle
  ```
  DEPENDENCY_REPO_URL=place_your_dependency_url
  DEPENDENCY_REPO_USERNAME=place_your_dependency_username
  DEPENDENCY_REPO_PASSWORD=place_your_dependency_password
  ```
    * If you decide publish common service yourself then credential to this repository
* Run
  ```
  gradlew publish
  ```