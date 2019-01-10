plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    api(project(":linda-core"))
    implementation("com.github.tony19:named-regexp:0.2.5")

    testImplementation("junit", "junit", "4.12")
    testImplementation(project(":linda-test"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_10
}