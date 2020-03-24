dependencies {
    api(Libs.`2p_core`)
    api(project(":linda-core"))
    api(project(":utils"))
    api(kotlin("stdlib-jdk8"))

    implementation(project(":prologx"))

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}