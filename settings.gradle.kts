pluginManagement {
    repositories {
        google()  // Убираем ограничения content
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repo.opencv.org/releases") }
        maven { url = uri("https://jitpack.io") }// Добавляем jitpack
    }
}

rootProject.name = "PhotoEditorDip"
include(":app")
include(":sdk")
