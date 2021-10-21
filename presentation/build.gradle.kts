dependencies {
    api("com.fasterxml.jackson.core:jackson-core:_")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:_")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:_")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:_")
    api(kotlin("stdlib-jdk8"))
    api(project(":utils"))

    testImplementation("junit:junit:_")
    testImplementation(project(":test-utils"))
}