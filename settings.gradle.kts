rootProject.name = "Socialismus"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.whereareiam.me/development")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        mavenCentral()

        maven("https://maven.whereareiam.me/release")
        maven("https://maven.whereareiam.me/development")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.william278.net/releases/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.codemc.io/repository/maven-releases/")
    }
}

include(":socialismus-api")
project(":socialismus-api").projectDir = file("socialismus-api")

include(":socialismus-common")
project(":socialismus-common").projectDir = file("socialismus-common")

include(":socialismus-integration")
project(":socialismus-integration").projectDir = file("socialismus-integration")

include(":socialismus-platform")
project(":socialismus-platform").projectDir = file("socialismus-platform")

include(":platform-bukkit")
project(":platform-bukkit").projectDir = file("socialismus-platform/platform-bukkit")

include(":socialismus-adapter-command")
project(":socialismus-adapter-command").projectDir = file("socialismus-adapter-command")

include(":socialismus-module")
project(":socialismus-module").projectDir = file("socialismus-module")

include(":socialismus-module:module-api")
project(":socialismus-module:module-api").projectDir = file("socialismus-module/module-api")

include(":socialismus-module:module")
project(":socialismus-module:module").projectDir = file("socialismus-module/module")

include(":socialismus-modules")
project(":socialismus-modules").projectDir = file("socialismus-modules")

include(":module-chirper")
project(":module-chirper").projectDir = file("socialismus-modules/module-chirper")

include(":module-chirper-api")
project(":module-chirper-api").projectDir = file("socialismus-modules/module-chirper/chirper-api")

include(":module-chirper-common")
project(":module-chirper-common").projectDir = file("socialismus-modules/module-chirper/chirper-common")

include(":module-chirper-command")
project(":module-chirper-command").projectDir = file("socialismus-modules/module-chirper/chirper-command")

include(":module-chirper-runtime")
project(":module-chirper-runtime").projectDir = file("socialismus-modules/module-chirper/chirper-bootstrap")

include(":module-bubbler")
project(":module-bubbler").projectDir = file("socialismus-modules/module-bubbler")

include(":module-bubbler-api")
project(":module-bubbler-api").projectDir = file("socialismus-modules/module-bubbler/bubbler-api")

include(":module-bubbler-common")
project(":module-bubbler-common").projectDir = file("socialismus-modules/module-bubbler/bubbler-common")

include(":module-bubbler-command")
project(":module-bubbler-command").projectDir = file("socialismus-modules/module-bubbler/bubbler-command")

include(":module-bubbler-runtime")
project(":module-bubbler-runtime").projectDir = file("socialismus-modules/module-bubbler/bubbler-bootstrap")

include(":integration-bstats")
project(":integration-bstats").projectDir = file("socialismus-integration/integration-bstats")

include(":integration-packetevents")
project(":integration-packetevents").projectDir = file("socialismus-integration/integration-packetevents")

include(":integration-papiproxybridge")
project(":integration-papiproxybridge").projectDir = file("socialismus-integration/integration-papiproxybridge")

include(":integration-placeholderapi")
project(":integration-placeholderapi").projectDir = file("socialismus-integration/integration-placeholderapi")

include(":platform-velocity")
project(":platform-velocity").projectDir = file("socialismus-platform/platform-velocity")

include(":platform-bukkit-common")
project(":platform-bukkit-common").projectDir = file("socialismus-platform/platform-bukkit/common")

include(":platform-bukkit-bukkit")
project(":platform-bukkit-bukkit").projectDir = file("socialismus-platform/platform-bukkit/bukkit")

include(":platform-bukkit-paper")
project(":platform-bukkit-paper").projectDir = file("socialismus-platform/platform-bukkit/paper")
