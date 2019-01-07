plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {

    api("junit", "junit", "4.12")
    api(project(":linda-core"))
    implementation(project(":test-utils"))
    implementation("org.javatuples:javatuples:1.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_10
}
