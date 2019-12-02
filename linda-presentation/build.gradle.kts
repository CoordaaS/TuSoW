val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
    api(project(":linda-text"))

    implementation(project(":utils"))
    implementation(project(":prologx"))

    implementation("com.fasterxml.jackson.core", "jackson-core", jacksonVersion)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", jacksonVersion)
    implementation("org.jooq", "jool", joolVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":test-utils"))
    testImplementation(project(":linda-test"))
    implementation(kotlin("stdlib-jdk8"))
}