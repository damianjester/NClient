pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "NClient"
include(":app")
