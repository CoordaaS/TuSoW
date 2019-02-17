group = "it.unibo.coordination"
version = "0.1.0"

allprojects {
    repositories {
        mavenCentral()
    }
}

fun toJavaVersion(versionString: String): JavaVersion {
    return try {
        JavaVersion.valueOf("VERSION_$versionString")
    } catch (e: IllegalArgumentException){
        JavaVersion.valueOf("VERSION_1_$versionString")
    }
}