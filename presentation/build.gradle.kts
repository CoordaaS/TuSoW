dependencies {
    api(Libs.jackson_core)
    api(Libs.jackson_datatype_jsr310)
    api(Libs.jackson_dataformat_xml)
    api(Libs.jackson_dataformat_yaml)
    api(kotlin("stdlib-jdk8"))
    api(project(":utils"))

    implementation(project(":prologx"))

    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
}