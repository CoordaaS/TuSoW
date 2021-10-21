plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
    api(project(":linda-logic-presentation"))
    api(project(":linda-text"))
    api(project(":linda-text-presentation"))
    api(kotlin("stdlib-jdk8"))
    api("io.vertx:vertx-core:_")
    api("io.vertx:vertx-web:_")

    implementation("ch.qos.logback:logback-classic:_")

    implementation("commons-cli:commons-cli:_")
//    implementation(Libs.jool)

    testImplementation("io.vertx:vertx-unit:_")
    testImplementation("junit:junit:_")
    testImplementation(project(":test-utils"))
}

val mainClassName = "it.unibo.coordination.tusow.Service"

task<JavaExec>("tusow") {
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
}

tasks.getByName<Jar>("shadowJar") {
    manifest {
        attributes("Main-Class" to mainClassName)
    }
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("redist")
    from(files("${rootProject.projectDir}/LICENSE"))
}
