dependencies {
    api(Libs.slf4j_api)
    api(Libs.kotlin_stdlib_jdk8)
    api(project(":control"))

    testImplementation(Libs.junit)
}
