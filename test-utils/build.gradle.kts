val javaVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(Libs.kotlin_stdlib_jdk8)
    api(Libs.junit)
    api(Libs.logback_classic)
}