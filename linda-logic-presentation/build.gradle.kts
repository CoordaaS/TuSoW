dependencies {
    api(project(":linda-logic"))
    api(project(":linda-core-presentation"))
    api(kotlin("stdlib-jdk8"))

    implementation(project(":prologx"))

    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
    testImplementation(project(":linda-test"))
}