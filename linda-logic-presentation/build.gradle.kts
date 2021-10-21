dependencies {
    api("it.unibo.tuprolog:serialize-core-jvm:_")
    api(kotlin("stdlib-jdk8"))
    api(project(":linda-logic"))
    api(project(":linda-core-presentation"))

    testImplementation("junit:junit:_")
    testImplementation(project(":test-utils"))
    testImplementation(project(":linda-test"))
}