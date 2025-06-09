plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.gpb"
version = "1.3.2"
java.sourceCompatibility = JavaVersion.VERSION_17

// Dependency versions
val commonGpbVersion = "1.3.1"

val lombokVersion = "1.18.24"
val jwtVersion = "4.3.0"
val jacksonAnnotationsVersion = "2.18.2"
val guavaVersion = "32.1.2-jre"
val jakartaMailVersion = "2.0.1"
val jakartaValidationVersion = "3.1.1"
val httpClientVersion = "5.2.1"
val jsoupVersion = "1.15.3"
val modelMapperVersion = "3.2.0"
val imageioWebpVersion = "3.10.1"
val postgresVersion = "42.5.1"
val flywayVersion = "9.22.0"
val hibernateSearchVersion = "6.2.3.Final"
val jbossLoggingVersion = "3.6.0.Final"
val jbossLoggingForceVersion = "3.4.5.Final"
val springLog4jVersion = "3.1.3"
val kafkaVersion = "3.1.1"
val h2Version = "2.2.220"
val mockitoVersion = "5.5.0"
val mockitoInlineVersion = "4.8.0"
val commonsIoVersion = "2.11.0"
val jacocoToolVersion = "0.8.11"

fun getEnvOrProperty(name: String): String {
    return findProperty(name)?.toString()
            ?: System.getenv(name)
            ?: throw GradleException("$name not specified. Set '$name' property or environment variable.")
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
    resolutionStrategy {
        force("org.jboss.logging:jboss-logging:$jbossLoggingForceVersion")
    }
}

dependencies {
    implementation("com.gpb:common:$commonGpbVersion")

    implementation("commons-io:commons-io:$commonsIoVersion")
    implementation("com.auth0:java-jwt:$jwtVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonAnnotationsVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("com.sun.mail:jakarta.mail:$jakartaMailVersion")
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")

    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("org.modelmapper:modelmapper:$modelMapperVersion")

    runtimeOnly("com.twelvemonkeys.imageio:imageio-webp:$imageioWebpVersion")

    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")

    implementation("org.hibernate.search:hibernate-search-mapper-orm-orm6:$hibernateSearchVersion")
    implementation("org.hibernate.search:hibernate-search-backend-lucene:$hibernateSearchVersion")
    implementation("org.jboss.logging:jboss-logging:$jbossLoggingVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:$springLog4jVersion")
    implementation("org.springframework.kafka:spring-kafka:$kafkaVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("org.mockito:mockito-inline:$mockitoInlineVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = jacocoToolVersion
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            excludes = listOf(
                    "**.configuration.*",
                    "**.entity.*",
                    "**.exception.*",
                    "**.util.*",
                    "com.gpb.game.controller.RestResponseEntityExceptionHandler",
                    "com.gpb.game.service.impl.ResourceServiceImpl",
                    "com.gpb.game.parser.StorePageParser",
                    "com.gpb.game.GpbStoresApplication"
            )
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}
