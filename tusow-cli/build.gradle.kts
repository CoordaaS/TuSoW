plugins {
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project(":linda-logic-client"))
    api(project(":linda-text-client"))
    api(kotlin("stdlib-jdk8"))
    api(Libs.clikt)

    implementation(Libs.slf4j_nop)

    testImplementation(Libs.junit)
    testImplementation(project(":tusow-service"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
}

val mainKlass = "it.unibo.coordination.tusow.Cli"

application {
    mainClassName = mainKlass
}

tasks.getByName<Jar>("shadowJar") {
    manifest {
        attributes("Main-Class" to mainKlass)
    }
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("redist")
    from(files("${rootProject.projectDir}/LICENSE"))
}