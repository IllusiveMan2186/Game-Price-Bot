plugins {
    java
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    id("maven-publish")
}

fun getEnvOrProperty(name: String): String {
    return project.findProperty(name)?.toString()
            ?: System.getenv(name)
            ?: throw GradleException("$name not specified. Set '$name' property or $name environment variable.")
}

group = "com.gpb"
version = "0.0.5-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
}

java {
    withSourcesJar()
    withJavadocJar()
}


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

tasks.withType<Test> {
    useJUnitPlatform()
}
