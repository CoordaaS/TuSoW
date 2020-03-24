dependencies {
    api(Libs.slf4j_api)
    api(project(":utils"))
    api(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
}
