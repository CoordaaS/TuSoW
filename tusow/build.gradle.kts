import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val logbackVersion: String by project
val vertxVersion: String by project
val commonsCliVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
//    api(project(":linda-objects"))
    api(project(":linda-presentation"))

    implementation(project(":utils"))
    implementation(project(":prologx"))

    api("io.vertx", "vertx-core", vertxVersion)
    api("io.vertx", "vertx-web", vertxVersion)
    runtimeOnly("ch.qos.logback", "logback-classic", logbackVersion)

    implementation("commons-cli", "commons-cli", commonsCliVersion)
    implementation("com.fasterxml.jackson.core", "jackson-core", jacksonVersion)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", jacksonVersion)
    implementation("org.jooq", "jool", joolVersion)

    // Use JUnit test framework
    testImplementation("io.vertx", "vertx-unit", vertxVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":test-utils"))
    implementation(kotlin("stdlib-jdk8"))
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

task<JavaExec>("tusow") {
    group = "run"
    dependsOn("classes", "compileKotlin")
    sourceSets {
        main {
            classpath = runtimeClasspath
        }
    }
    main = "it.unibo.coordination.tusow.Service"
    if (project.hasProperty("port")) {
        args = listOf("-p", project.property("port").toString())
    }
    standardInput = System.`in`
}
repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = javaVersion
        freeCompilerArgs = ktFreeCompilerArgs.split(";").toList()
    }
}