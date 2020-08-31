dependencies {
    api("it.unibo.tuprolog", "serialize-core-jvm", Versions.it_unibo_tuprolog)
    api(project(":linda-logic"))
    api(project(":linda-core-presentation"))
    api(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
    testImplementation(project(":linda-test"))
}