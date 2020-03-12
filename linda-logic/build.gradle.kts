dependencies {
    api(Libs.`2p_core`)
    api(project(":linda-core"))

    implementation(project(":utils"))
    implementation(project(":prologx"))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}