pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LearnApp"
include(":app")
include(":core:model")
include(":core:common")
include(":core:domain")
include(":core:network")
include(":core:data")
include(":core:datastore")
include(":core:ui")
include(":core:startup")
include(":core:profiling")
include(":feature:splash")
include(":feature:auth")
include(":feature:children")
include(":feature:home")
include(":feature:tasks")
include(":feature:daily")
include(":feature:summary")
