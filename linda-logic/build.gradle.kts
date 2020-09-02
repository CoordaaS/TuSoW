dependencies {
    api(Libs.theory_jvm)
    api(Libs.parser_core_jvm)
    api(project(":linda-core"))
    api(project(":utils"))
    api(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}