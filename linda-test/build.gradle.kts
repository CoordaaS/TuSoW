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
    api(project(":test-utils"))
    api("org.javatuples:javatuples:1.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_10
}
