dependencies {
    api(Libs.commons_collections4)
    api(Libs.slf4j_api)
    api(Libs.kotlin_stdlib_jdk8)
    api(project(":utils"))
    api(project(":control"))

    testImplementation(Libs.junit)
}
