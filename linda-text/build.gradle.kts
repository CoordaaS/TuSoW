dependencies {
    api(project(":linda-core"))
    api(kotlin("stdlib-jdk8"))
    api("com.github.tony19:named-regexp:_")

    testImplementation("junit:junit:_")
    testImplementation(project(":linda-test"))
}