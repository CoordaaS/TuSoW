dependencies {
    api(Libs.junit)
    api(project(":linda-core"))
    api(project(":utils"))
    api(project(":test-utils"))
    api(Libs.javatuples)

    implementation(kotlin("stdlib-jdk8"))
}