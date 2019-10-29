plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

val javaVersion: String by project
val namedRegexpVersion: String by project
val junitVersion: String by project

dependencies {
    api(project(":linda-core"))
    implementation("com.github.tony19", "named-regexp", namedRegexpVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":linda-test"))
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}