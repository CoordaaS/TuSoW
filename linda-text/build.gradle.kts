dependencies {
    api(project(":linda-core"))
    api(kotlin("stdlib-jdk8"))
    api(Libs.named_regexp)

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}