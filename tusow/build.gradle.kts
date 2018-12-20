plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
    api(project(":linda-objects"))

    implementation(project(":utils"))
    implementation(project(":prologx"))

    api("io.vertx:vertx-core:3.6.2")
    api("io.vertx:vertx-web:3.6.2")

    implementation("com.fasterxml.jackson.core:jackson-core:2.9.8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.8")

    // Use JUnit test framework
    testImplementation("io.vertx:vertx-unit:3.5.1")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("junit", "junit", "4.12")
    testImplementation(project(":test-utils"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_10
}
