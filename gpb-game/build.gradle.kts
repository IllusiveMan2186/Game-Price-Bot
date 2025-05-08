plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.gpb"
version = "1.2.1"
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
    resolutionStrategy {
        force("org.jboss.logging:jboss-logging:3.4.5.Final")
    }
}

dependencies {
    implementation("com.gpb:common:1.2.1")

    implementation("commons-io:commons-io:2.11.0")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
    implementation("com.google.guava:guava:32.1.2-jre")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")

    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.modelmapper:modelmapper:3.2.0")

    runtimeOnly("com.twelvemonkeys.imageio:imageio-webp:3.10.1")

    implementation("org.postgresql:postgresql:42.5.1")
    implementation("org.flywaydb:flyway-core")

    implementation("org.hibernate.search:hibernate-search-mapper-orm:7.2.2.Final")
    implementation("org.hibernate.search:hibernate-search-backend-lucene:7.2.2.Final")
    implementation("org.jboss.logging:jboss-logging:3.6.0.Final")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:3.1.3")

    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("com.h2database:h2:2.2.220")
    testImplementation("org.mockito:mockito-inline:4.8.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.11"
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