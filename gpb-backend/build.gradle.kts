plugins {
    java
    war
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.gpb"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

fun getEnvOrProperty(name: String): String {
    return findProperty(name)?.toString()
            ?: System.getenv(name)
            ?: throw GradleException("$name not specified. Set '$name' property or $name environment variable.")
}

repositories {
    mavenCentral()
    maven {
        isAllowInsecureProtocol = true
        url = uri(getEnvOrProperty("DEPENDENCY_REPO_URL"))
        credentials {
            username = getEnvOrProperty("DEPENDENCY_REPO_USERNAME")
            password = getEnvOrProperty("DEPENDENCY_REPO_PASSWORD")
        }
    }
}
configurations.all {
    exclude(module = "spring-boot-starter-logging")
}

dependencies {
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    implementation("com.google.guava:guava:32.1.2-jre")
    implementation("org.modelmapper:modelmapper:3.2.0")
    implementation("org.seleniumhq.selenium:selenium-java:4.16.1")
    implementation("io.github.bonigarcia:webdrivermanager:5.7.0")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("org.flywaydb:flyway-core")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:3.1.3")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-web")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework:spring-context")
    implementation("org.springframework.security:spring-security-config")
    implementation("org.springframework.kafka:spring-kafka")

    implementation("org.postgresql:postgresql:42.5.1")

    implementation("com.gpb:common:0.0.2-SNAPSHOT")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("com.h2database:h2:2.2.220")
}

tasks.withType<Test> {
    useJUnitPlatform {
        excludeTags("e2e") // Exclude tests with the @Tag("e2e") annotation
    }
}

tasks.register<Test>("integrationTest") {
    group = "verification"
    description = "Runs integration tests."
    useJUnitPlatform {
        includeTags("integration") // Only include tests tagged with @Tag("integration")
    }
}

tasks.register<Test>("e2eTest") {
    group = "verification"
    description = "Runs end-to-end tests."
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    doFirst {
        val username = System.getenv("E2E_EMAIL")
                ?: throw IllegalStateException("Property 'E2E_EMAIL' is required")
        val password = System.getenv("E2E_PASSWORD")
                ?: throw IllegalStateException("Property 'E2E_PASSWORD' is required")

        systemProperty("e2e.email", username)
        systemProperty("e2e.password", password)
    }
    useJUnitPlatform {
        includeTags("e2e") // Only include tests tagged with @Tag("e2e")
    }
}
