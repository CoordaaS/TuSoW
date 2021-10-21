dependencies {
    api("it.unibo.tuprolog:theory-jvm:_")
    api("it.unibo.tuprolog:parser-core-jvm:_")
    api(project(":linda-core"))
    api(project(":utils"))
    api(kotlin("stdlib-jdk8"))

    testImplementation("junit:junit:_")
    testImplementation(project(":linda-test"))
}