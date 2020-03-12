plugins {
    application
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

application {
    mainClassName = "it.unibo.coordination.linda.cli.TusowCommandKt"
}

