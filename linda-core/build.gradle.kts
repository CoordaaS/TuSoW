plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

//repositories {
//    mavenCentral()
//}

dependencies {
    api("it.unibo.alice.tuprolog", "tuprolog", "3.3.0")
    api("org.apache.commons", "commons-collections4", "4.2")

    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_10
    targetCompatibility = JavaVersion.VERSION_1_10
}
