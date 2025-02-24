plugins {
    java
    war
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.gpb"
version = "1.1.0"
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
    implementation("com.gpb:common:1.1.0")

    implementation("commons-io:commons-io:2.11.0")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.7.0")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.modelmapper:modelmapper:3.2.0")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("org.seleniumhq.selenium:selenium-java:4.12.0")
    implementation("org.seleniumhq.selenium:selenium-devtools-v116:4.12.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
    implementation("org.springframework.boot:spring-boot-starter-aop")


    implementation("org.springframework:spring-context")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.security:spring-security-config")
    implementation("org.springframework.retry:spring-retry")

    testImplementation("com.h2database:h2:2.2.220")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform {
        excludeTags("e2e")
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
        includeTags("e2e")
    }
}
