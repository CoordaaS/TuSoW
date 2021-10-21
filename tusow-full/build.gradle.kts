plugins {
    id("com.github.johnrengelman.shadow")
}

val tusowFull = name

dependencies {
    rootProject.subprojects
            .map { it.name }
            .filter { it != tusowFull }
            .forEach {
                api(project(":$it"))
            }

    implementation("ch.qos.logback:logback-classic:_")
}

tasks.getByName<Jar>("shadowJar") {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("redist")
    from(files("${rootProject.projectDir}/LICENSE"))
}