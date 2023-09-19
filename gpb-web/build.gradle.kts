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

dependencies {
	compileOnly("org.projectlombok:lombok:1.18.24")
	annotationProcessor("org.projectlombok:lombok:1.18.24")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.postgresql:postgresql:42.5.1")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

	// https://mvnrepository.com/artifact/org.flywaydb/flyway-core
	implementation("org.flywaydb:flyway-core:8.5.13")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
