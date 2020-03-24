import org.gradle.api.Project

private val FULL_VERSION_REGEX = "^[0-9]+\\.[0-9]+\\.[0-9]+$".toRegex()

fun Project.getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        System.err.println("WARNING: $key is not set")
    }
    return value
}

fun Iterable<Project>.forEachProject(action: Project.() -> Unit) {
    this.forEach { it.action() }
}

fun Project.subprojects(vararg names: String, action: Project.() -> Unit) {
    for (name in names) {
        project(":$name", action)
    }
}

fun Project.subprojects(vararg names: String): Iterable<Project> =
        names.map { project(":$it") }

val Project.isFullVersion: Boolean
    get() = version.toString().matches(FULL_VERSION_REGEX)