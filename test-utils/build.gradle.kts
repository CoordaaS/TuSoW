val javaVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(kotlin("stdlib-jdk8"))
    api("junit:junit:_")
    api("ch.qos.logback:logback-classic:_")
}