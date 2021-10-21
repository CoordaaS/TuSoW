dependencies {
    api(project(":linda-logic"))
    api(project(":linda-remote-client"))
    api(project(":linda-logic-presentation"))
    api(kotlin("stdlib-jdk8"))
    api("io.vertx:vertx-web-client:_")

    testImplementation("junit:junit:_")
    testImplementation(project(":tusow-service"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
}