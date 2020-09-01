dependencies {
    api(Libs.serialize_core_jvm)
    api(Libs.kotlin_stdlib_jdk8)
    api(project(":linda-logic"))
    api(project(":linda-core-presentation"))

    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
    testImplementation(project(":linda-test"))
}