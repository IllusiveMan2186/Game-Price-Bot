# Email part
Part of GPB application that responsible for sending emails

### Setup for development environment:

* Kafka 3.6.1 or higher
* JDK 17 or higher
* Gradle 8.0.2 or higher

## Step for running in development environment:
* Launch Kafka server message broker
* Run service

### Environments

Add Environment variables for running project locally

* MAIL_USERNAME: The username for sending emails (e.g., Gmail address).
    * Enter the email address you will use to send notifications to users.


* MAIL_PASSWORD: The app password for the email account used for sending emails.
    * Set the correct password for the email account here.


* KAFKA_SERVER_URL: The URL of the Kafka server for your backend to communicate with.
    * Set the correct Kafka server URL if it is different from the default.