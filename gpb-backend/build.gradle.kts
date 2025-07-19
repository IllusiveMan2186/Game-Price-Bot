plugins {
    java
    kotlin("jvm") version "1.8.10"
    id("jacoco")
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.gpb"
version = "1.3.3"
java.sourceCompatibility = JavaVersion.VERSION_17

// --- Versions ---
val commonGpbVersion = "1.3.1"

val lombokVersion = "1.18.24"
val jwtVersion = "4.3.0"
val seleniumVersion = "4.12.0"
val webdrivermanagerVersion = "5.7.0"
val httpClientVersion = "5.2.1"
val jsoupVersion = "1.15.3"
val modelMapperVersion = "3.2.0"
val postgresVersion = "42.5.1"
val jakartaValidationVersion = "3.1.1"
val hibernateValidatorVersion = "8.0.1.Final"
val h2Version = "2.2.220"
val mockitoVersion = "5.5.0"
val mockitoInlineVersion = "4.8.0"
val commonsIoVersion = "2.11.0"
val jacocoVersion = "0.8.11"

// --- Env Resolver ---
fun getEnvOrProperty(name: String): String {
    return findProperty(name)?.toString()
            ?: System.getenv(name)
            ?: throw GradleException("$name not specified. Set '$name' property or $name environment variable.")
}

// --- Repositories ---
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

// --- Dependencies ---
dependencies {
    implementation("com.gpb:common:$commonGpbVersion")

    implementation("commons-io:commons-io:$commonsIoVersion")
    implementation("com.auth0:java-jwt:$jwtVersion")
    implementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("org.modelmapper:modelmapper:$modelMapperVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    implementation("org.seleniumhq.selenium:selenium-devtools-v116:$seleniumVersion")
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springframework:spring-context")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.security:spring-security-config")
    implementation("org.springframework.retry:spring-retry")

    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testImplementation("org.mockito:mockito-inline:$mockitoInlineVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

// --- Tasks ---
tasks {
    jar { enabled = false }
    bootJar {
        archiveFileName.set("app.jar")
    }
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
        val url = System.getenv("E2E_URL")
                ?: throw IllegalStateException("Property 'E2E_URL' is required")
        systemProperty("e2e.email", username)
        systemProperty("e2e.password", password)
        systemProperty("e2e.url", url)
    }
    useJUnitPlatform {
        includeTags("e2e")
    }
}

// --- Jacoco ---
jacoco { toolVersion = jacocoVersion }

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"

            excludes = listOf(
                    "**.configuration.*",
                    "**.entity.*",
                    "**.exception.*",
                    "**.util.*",
                    "com.gpb.backend.controller.RestResponseEntityExceptionHandler",
                    "com.gpb.backend.ServletInitializer",
                    "com.gpb.backend.GpbWebApplication"
            )

            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}
