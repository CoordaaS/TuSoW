dependencies {
    api(project(":linda-core"))
    api(project(":presentation"))
    api(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
}