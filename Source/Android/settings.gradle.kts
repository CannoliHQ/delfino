pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
include(":benchmark")

include(":cannoli-igm")
project(":cannoli-igm").projectDir = file("../../Externals/cannoli/cannoli-igm")

include(":cannoli-ui")
project(":cannoli-ui").projectDir = file("../../Externals/cannoli/cannoli-ui")

include(":cannoli-core")
project(":cannoli-core").projectDir = file("../../Externals/cannoli/cannoli-core")
