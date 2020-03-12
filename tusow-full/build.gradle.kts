dependencies {
    rootProject.subprojects.map { it.name }.forEach {
        api(project(":$it"))
    }
}

tasks.getByName<Jar>("shadowJar") {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("redist")
    from(files("${rootProject.projectDir}/LICENSE"))
}