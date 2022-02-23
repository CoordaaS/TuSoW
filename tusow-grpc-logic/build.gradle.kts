plugins {
    kotlin("jvm")
    id("com.google.protobuf") version "0.8.18"
    "java-library"
    // ASSUMES GRADLE 5.6 OR HIGHER. Use plugin version 0.8.10 with earlier gradle versions
    // Generate IntelliJ IDEA's .idea & .iml project files
    "idea"
}

group = "it.unibo.coordaas.tusow.grpc.logic"
version = "0.7.2-dev08+29f0ef4"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
    api(project(":linda-logic-presentation"))
    api(project(":linda-text"))
    api(project(":linda-text-presentation"))
    api(project(":tusow-service"))
    api(project(":tusow-grpc-presentation"))
    api(project(":tusow-grpc-presentation"))
    implementation("ch.qos.logback:logback-classic:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    testImplementation("junit:junit:4.12")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}