dependencies {
    api(project(":linda-core"))
    api(project(":presentation"))
    api(kotlin("stdlib-jdk8"))

    testImplementation("junit:junit:_")
    testImplementation(project(":test-utils"))
}