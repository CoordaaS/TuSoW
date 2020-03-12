val javaVersion: String by project
val tuprologVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(Libs.`2p_presentation`)

    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junit)
}