dependencies {
    api(project(":linda-text"))
    api(project(":linda-core-presentation"))

    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
}