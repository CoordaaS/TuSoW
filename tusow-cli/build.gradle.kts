plugins {
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project(":linda-logic-client"))
    api(project(":linda-text-client"))

    implementation(Libs.clikt)
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":tusow"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
}

val mainClass = "it.unibo.coordination.linda.cli.TusowCommandKt"

application {
    mainClassName = mainClass
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