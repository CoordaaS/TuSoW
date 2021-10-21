dependencies {
    api("org.apache.commons:commons-collections4:_")
    api("org.slf4j:slf4j-api:_")
    api(project(":utils"))

    api(kotlin("stdlib-jdk8"))

    testImplementation("junit:junit:_")
}
