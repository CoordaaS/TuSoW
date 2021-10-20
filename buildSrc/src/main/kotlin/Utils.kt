import org.gradle.api.Project

private val alreadyWarned = mutableSetOf<String>()

fun Project.getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank() && key !in alreadyWarned) {
        System.err.println("WARNING: $key is not set")
        alreadyWarned += key
    }
    return value
}
