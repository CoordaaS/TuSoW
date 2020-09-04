dependencies {
    api(project(":respect-core"))
    api(project(":linda-logic"))
    api(Libs.solve_classic_jvm)

    testImplementation(Libs.junit)
    testImplementation(project(":linda-test"))
}