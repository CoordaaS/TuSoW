dependencies {
    api(project(":linda-logic"))
    api(project(":linda-core-presentation"))

    implementation(project(":prologx"))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
    testImplementation(project(":linda-test"))
}