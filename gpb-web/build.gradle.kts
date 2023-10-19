plugins {
	java
	war
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.gpb"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

configurations.all {
	exclude(module = "spring-boot-starter-logging")
}

dependencies {
	implementation("com.auth0:java-jwt:4.3.0")
	implementation("org.jsoup:jsoup:1.15.3")

	compileOnly("org.projectlombok:lombok:1.18.24")
	annotationProcessor("org.projectlombok:lombok:1.18.24")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-log4j2:3.1.3")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-config")


	implementation("org.postgresql:postgresql:42.5.1")
	implementation("org.flywaydb:flyway-core:8.5.13")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
	testImplementation("com.h2database:h2:2.2.220")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
