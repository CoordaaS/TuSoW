dependencies {
    api(project(":linda-core"))

    implementation(project(":utils"))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}