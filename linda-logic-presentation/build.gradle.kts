repositories {
    maven("https://dl.bintray.com/pika-lab/tuprolog/")
}

dependencies {
    api(project(":linda-logic"))
    api(project(":linda-core-presentation"))
    api(kotlin("stdlib-jdk8"))
    api("it.unibo.tuprolog", "serialization-jvm", Versions.it_unibo_tuprolog)

    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
    testImplementation(project(":linda-test"))
}