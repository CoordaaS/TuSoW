val javaVersion: String by project
val javaTuplesVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {

    api("junit", "junit", junitVersion)
    api(project(":linda-core"))
    api(project(":utils"))
    api(project(":test-utils"))
    api("org.javatuples", "javatuples", javaTuplesVersion)
    implementation(kotlin("stdlib-jdk8"))
}