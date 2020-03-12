dependencies {
    api(Libs.jackson_core)
    api(Libs.jackson_datatype_jsr310)
    api(Libs.jackson_dataformat_xml)
    api(Libs.jackson_dataformat_yaml)

    implementation(project(":utils"))
    implementation(project(":prologx"))
    implementation(kotlin("stdlib-jdk8"))


    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
}