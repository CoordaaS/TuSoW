dependencies {
    api(project(":linda-core"))
    api(project(":presentation"))
    api(Libs.vertx_web_client)

    implementation(kotlin("stdlib-jdk8"))


    testImplementation(Libs.junit)
}