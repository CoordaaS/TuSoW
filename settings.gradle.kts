import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("de.fayard:dependencies:0.+")
    }
}

bootstrapRefreshVersionsAndDependencies()

rootProject.name = "coordaas"

include("utils")
include("linda-core")
include("linda-logic")
include("linda-text")
include("test-utils")
include("linda-test")
include("presentation")
include("linda-core-presentation")
include("linda-logic-presentation")
include("linda-text-presentation")
include("linda-remote-client")
include("linda-logic-client")
include("linda-text-client")
include("tusow-service")
include("tusow-cli")
include("tusow-grpc")
include("tusow-full")
include("tusow-grpc-presentation")
include("tusow-grpc-text")
include("tusow-grpc-text")
include("tusow-grpc-logic")
