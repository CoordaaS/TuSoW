dependencies {
    api(project(":linda-core"))
    api(project(":presentation"))
    api("io.vertx:vertx-web-client:_")
    api(kotlin("stdlib-jdk8"))

    testImplementation("junit:junit:_")
}