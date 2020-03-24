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
    api(Libs.vertx_core)
    api(Libs.vertx_web)

    implementation(Libs.logback_classic)

    implementation(project(":prologx"))
    implementation(Libs.commons_cli)
    implementation(Libs.jackson_core)
    implementation(Libs.jackson_datatype_jsr310)
    implementation(Libs.jackson_dataformat_xml)
    implementation(Libs.jackson_dataformat_yaml)
    implementation(Libs.jool)

    testImplementation(Libs.vertx_unit)
    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
}

val mainClass = "it.unibo.coordination.tusow.Service"

task<JavaExec>("tusow") {
    group = "run"
    dependsOn("classes", "compileKotlin")
    sourceSets {
        main {
            classpath = runtimeClasspath
        }
    }
    main = mainClass
    if (project.hasProperty("port")) {
        args = listOf("-p", project.property("port").toString())
    }
    standardInput = System.`in`
}

tasks.getByName<Jar>("shadowJar") {
    manifest {
        attributes("Main-Class" to mainClass)
    }
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("redist")
    from(files("${rootProject.projectDir}/LICENSE"))
}
