
# Game part

Part of GPB application that responsible for handling with game entities

## Setup for development environment:

* PostgreSQL latest version
* Kafka 3.6.1 or higher
* JDK 17 or higher
* Gradle 8.0.2 or higher

## Step for running in development environment:
* Launch Kafka server message broker
* Launch PostgreSQL server
* Run service

### Environments

Add Environment variables for running project locally

* IMAGE_FOLDER: The folder path for storing game-related images.
    * Set your desired folder path here.


* KAFKA_SERVER_URL: The URL of the Kafka server for your backend to communicate with.
    * Set the correct Kafka server URL if it is different from the default.


* POSTGRES_DB_URL: The JDBC URL to connect to the PostgreSQL database.
    * You typically do not need to change this unless you are running PostgreSQL on a different host or port.


* POSTGRES_DB_USERNAME: The username to connect to the PostgreSQL database.
    * Set your desired username here.


* POSTGRES_DB_PASSWORD: The password for the PostgreSQL user.
    * Set a strong password for your PostgreSQL user here.


* API_KEY: The API key used for authorization or other purposes.
    * Set your unique API key here.