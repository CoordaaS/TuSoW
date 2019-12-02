val javaVersion: String by project
val namedRegexpVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(project(":linda-core"))
    implementation("com.github.tony19", "named-regexp", namedRegexpVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":linda-test"))
    implementation(kotlin("stdlib-jdk8"))
}