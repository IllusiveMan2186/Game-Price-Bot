plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    id("maven-publish")
}

group = "com.gpb"
version = "1.3.1"
java.sourceCompatibility = JavaVersion.VERSION_17

// --- Versions ---
val lombokVersion = "1.18.24"
val jakartaValidationVersion = "3.1.1"
val kafkaVersion = "3.0.4" // aligns with Spring Boot 3.0.4
val mockitoVersion = "5.5.0"
val jacocoVersion = "0.8.11"

// --- Env Resolver ---
fun getEnvOrProperty(name: String): String {
    return project.findProperty(name)?.toString()
            ?: System.getenv(name)
            ?: throw GradleException("$name not specified. Set '$name' property or environment variable.")
}

// --- Repositories ---
repositories {
    mavenCentral()
}

// --- Dependencies ---
dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
}

// --- Java Artifacts ---
java {
    withSourcesJar()
    withJavadocJar()
}

// --- Publishing ---
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.gpb"
            artifactId = "common"
            version = project.version.toString()

            pom {
                name.set("Common")
                description.set("Service with common classes of GPB")
                url.set(getEnvOrProperty("DEPENDENCY_REPO_URL"))
            }

            versionMapping {
                usage("java-api") {
                    fromResolutionResult()
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }

    repositories {
        maven {
            isAllowInsecureProtocol = true
            url = uri(getEnvOrProperty("DEPENDENCY_REPO_URL"))
            credentials {
                username = getEnvOrProperty("DEPENDENCY_REPO_USERNAME")
                password = getEnvOrProperty("DEPENDENCY_REPO_PASSWORD")
            }
        }
    }
}

// --- Test ---
tasks.withType<Test> {
    useJUnitPlatform()
}

// --- Jacoco ---
jacoco {
    toolVersion = jacocoVersion
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"

            excludes = listOf(
                    "**.entity.*",
                    "**.exception.*",
                    "**.util.*"
            )

            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}
