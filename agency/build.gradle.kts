dependencies {
    api(Libs.slf4j_api)
    api(project(":control"))
    api(Libs.kotlin_stdlib_jdk8)

    testImplementation(Libs.junit)
}
