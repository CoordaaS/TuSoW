dependencies {
    api(Libs.slf4j_api)
    api(project(":control"))
    api(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
}
