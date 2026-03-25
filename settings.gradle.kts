pluginManagement {
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.whereareiam.me/development")
        maven("https://repo.william278.net/releases/")
        maven("https://maven.whereareiam.me/release")
        maven("https://jitpack.io")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Socialismus"

include("socialismus-integration:integration-papiproxybridge")
include("socialismus-integration:integration-placeholderapi")
include("socialismus-integration:integration-packetevents")
include("socialismus-integration:integration-bstats")
include("socialismus-platform:platform-velocity")
include("socialismus-platform:platform-bukkit:common")
include("socialismus-platform:platform-bukkit:bukkit")
include("socialismus-platform:platform-bukkit:paper")
include("socialismus-platform:platform-bukkit")
include("socialismus-adapter-command")
include("socialismus-adapter-module")
include("socialismus-common")
include("socialismus-api")
