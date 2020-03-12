dependencies {
    api(project(":linda-text"))
    api(project(":linda-remote-client"))
    api(project(":linda-text-presentation"))
    api(Libs.vertx_web_client)

    implementation(kotlin("stdlib-jdk8"))


    testImplementation(Libs.junit)
    testImplementation(project(":tusow"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
}