pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "KMM_News"
include(":androidApp")
include(":shared")
include(":desktopApp")
include(":webApp")