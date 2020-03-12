val javaVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    implementation(Libs.junit)
    implementation(kotlin("stdlib-jdk8"))
}