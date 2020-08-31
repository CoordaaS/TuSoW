repositories {
    maven("https://dl.bintray.com/pika-lab/tuprolog/")
}

dependencies {
    api("it.unibo.tuprolog", "theory-jvm", Versions.it_unibo_tuprolog)
    api("it.unibo.tuprolog", "parser-core-jvm", Versions.it_unibo_tuprolog)
    api(project(":linda-core"))
    api(project(":utils"))
    api(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}