dependencies {
    api("junit:junit:_")
    api(project(":linda-core"))
    api(project(":utils"))
    api(project(":test-utils"))
    api("org.javatuples:javatuples:_")

    implementation(kotlin("stdlib-jdk8"))
}