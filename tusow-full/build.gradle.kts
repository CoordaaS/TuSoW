val tusowFull = name

dependencies {
    rootProject.subprojects
            .map { it.name }
            .filter { it != tusowFull }
            .forEach {
                api(project(":$it"))
            }

    implementation(Libs.logback_classic)
}

tasks.getByName<Jar>("shadowJar") {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("redist")
    from(files("${rootProject.projectDir}/LICENSE"))
}