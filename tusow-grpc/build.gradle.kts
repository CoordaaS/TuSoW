import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    kotlin("jvm")
    id("com.google.protobuf") version "0.8.18"
    "java-library"
    // ASSUMES GRADLE 5.6 OR HIGHER. Use plugin version 0.8.10 with earlier gradle versions
    // Generate IntelliJ IDEA's .idea & .iml project files
    "idea"
}

group "it.unibo.coordaas"
version "0.6.0-dev0i+a58a0a0"

repositories {
    mavenCentral()
    mavenLocal()
}

// IMPORTANT: You probably want the non-SNAPSHOT version of gRPC. Make sure you
// are looking at a tagged version of the example and not "master"!

// Feel free to delete the comment at the next line. It is just for safely
// updating the version in our release process.
val grpcVersion = "1.43.2" // CURRENT_GRPC_VERSION
val protobufVersion = "3.19.3"
val protocVersion = protobufVersion
val grpcKotlinVersion = "1.2.1"

dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
    api(project(":linda-logic-presentation"))
    api(project(":linda-text"))
    api(project(":linda-text-presentation"))
    api(project(":tusow-service"))
    api(project(":linda-grpc-presentation"))
    api(project(":linda-text-grpc-client"))
    api(project(":linda-logic-grpc-client"))
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")

    // examples/advanced need this for JsonFormat
    implementation("ch.qos.logback:logback-classic:_")

    testImplementation("junit:junit:4.12")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val mainClassName = "it.unibo.coordination.tusow.grpc.Server"

task<JavaExec>("run") {
    group = "run"
    dependsOn("classes", "compileKotlin")
    sourceSets {
        main {
            classpath = runtimeClasspath
        }
    }
    main = mainClassName
    if (project.hasProperty("port")) {
        args = listOf("-p", project.property("port").toString())
    }
    standardInput = System.`in`
    standardOutput = System.`out`
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

tasks.withType<Test> {
    testLogging.showStandardStreams = true
}

task("prepareKotlinBuildScriptModel") {}