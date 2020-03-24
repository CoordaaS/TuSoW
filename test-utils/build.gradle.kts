val javaVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(Libs.junit)
}