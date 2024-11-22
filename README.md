# Game Price Bot

 Game price bot is web application that combine information from available stores about video games and allow user to see best price.
 Also inform user via email or messenger about any changes in information about interesting game.

Currently, support only gamezey store

# You can run the app using Docker or launch it directly in your development environment.

## Setup for development environment:

* PostgreSQL latest version
* Zookeeper 3.9.1 or higher
* Kafka 3.6.1 or higher
* Node 18.18.0
* JDK 17 or higher
* Apache Maven 3.9.0 or higher
* Gradle 8.0.2 or higher

## Step for running in development environment:

* Launch PostgreSQL server
* Start zookeeper 
* Start kafka 
* Start all needed services 

## Docker Setup:

### To run GPB in Docker after installing, create a ".env" file and use the following command in root of project:

```console
docker-compose up
```

Environments in ".env" file :
* POSTGRES_DB_USERNAME;
* POSTGRES_DB_PASSWORD;
* GAMEZEY_LOGIN;
* GAMEZEY_PASSWORD;
* MAIL_USERNAME;
* MAIL_PASSWORD;

You also could stop some docker parts and run part on yours development environment