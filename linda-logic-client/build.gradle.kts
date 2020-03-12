dependencies {
    api(project(":linda-logic"))
    api(project(":linda-remote-client"))
    api(project(":linda-logic-presentation"))
    api(Libs.vertx_web_client)

    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":prologx"))


    testImplementation(Libs.junit)
    testImplementation(project(":tusow"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
}