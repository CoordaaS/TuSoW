dependencies {
    api(Libs.theory_jvm)
    api(Libs.parser_core_jvm)
    api(Libs.kotlin_stdlib_jdk8)
    api(project(":linda-core"))
    api(project(":utils"))

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}