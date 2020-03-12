dependencies {
    api(project(":linda-core"))

    implementation(Libs.named_regexp)
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}