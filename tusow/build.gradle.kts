dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
    api(project(":linda-logic-presentation"))
    api(project(":linda-text"))
    api(project(":linda-text-presentation"))
    api(Libs.vertx_core)
    api(Libs.vertx_web)

    runtimeOnly(Libs.logback_classic)

    implementation(project(":prologx"))
    implementation(Libs.commons_cli)
    implementation(Libs.jackson_core)
    implementation(Libs.jackson_datatype_jsr310)
    implementation(Libs.jackson_dataformat_xml)
    implementation(Libs.jackson_dataformat_yaml)
    implementation(Libs.jool)
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.vertx_unit)
    testImplementation(Libs.junit)
    testImplementation(project(":test-utils"))
}

task<JavaExec>("tusow") {
    group = "run"
    dependsOn("classes", "compileKotlin")
    sourceSets {
        main {
            classpath = runtimeClasspath
        }
    }
    main = "it.unibo.coordination.tusow.Service"
    if (project.hasProperty("port")) {
        args = listOf("-p", project.property("port").toString())
    }
    standardInput = System.`in`
}