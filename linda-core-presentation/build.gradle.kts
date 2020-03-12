dependencies {
    api(project(":linda-core"))
    api(project(":presentation"))

    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
}