dependencies {
    api(project(":linda-logic"))
    api(project(":linda-remote-client"))
    api(project(":linda-logic-presentation"))
    api(kotlin("stdlib-jdk8"))
    api(Libs.vertx_web_client)

    testImplementation(Libs.junit)
    testImplementation(project(":tusow-service"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
}