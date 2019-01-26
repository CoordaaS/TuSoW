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
    api(project(":linda-core"))
    implementation(project(":utils"))
    implementation(project(":prologx"))

    testImplementation("junit", "junit", "4.12")
    testImplementation(project(":linda-test"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_10
    targetCompatibility = JavaVersion.VERSION_1_10
}
