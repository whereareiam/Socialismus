rootProject.name = "Socialismus"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://registry.whereareiam.me/maven/packages")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        mavenCentral()

        maven("https://registry.whereareiam.me/maven/packages")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.william278.net/releases/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.opencollab.dev/main/")
        maven("https://repo.opencollab.dev/maven-snapshots/")

        // Plugin jars as their authors publish them, such as PacketEvents for the Bubbler journeys.
        exclusiveContent {
            forRepository { maven("https://api.modrinth.com/maven") }
            filter { includeGroup("maven.modrinth") }
        }
    }
}

include(":socialismus-api")
project(":socialismus-api").projectDir = file("socialismus-api")

include(":socialismus-testing")
project(":socialismus-testing").projectDir = file("socialismus-testing")
include(":fixture-module-library")
project(":fixture-module-library").projectDir = file("socialismus-testing/fixture/module-library")
include(":fixture-module-consumer")
project(":fixture-module-consumer").projectDir = file("socialismus-testing/fixture/module-consumer")

include(":socialismus-common")
project(":socialismus-common").projectDir = file("socialismus-common")

include(":socialismus-integration")
project(":socialismus-integration").projectDir = file("socialismus-integration")

include(":socialismus-platform")
project(":socialismus-platform").projectDir = file("socialismus-platform")

include(":socialismus-platform:bundle")
project(":socialismus-platform:bundle").projectDir = file("socialismus-platform/bundle")

include(":socialismus-platform:common")
project(":socialismus-platform:common").projectDir = file("socialismus-platform/common")

include(":platform-bukkit")
project(":platform-bukkit").projectDir = file("socialismus-platform/type/platform-bukkit")

include(":platform-proxy")
project(":platform-proxy").projectDir = file("socialismus-platform/type/platform-proxy")

include(":socialismus-adapter-command")
project(":socialismus-adapter-command").projectDir = file("socialismus-adapter-command")

include(":socialismus-module")
project(":socialismus-module").projectDir = file("socialismus-module")

include(":socialismus-module:module-api")
project(":socialismus-module:module-api").projectDir = file("socialismus-module/module-api")

include(":socialismus-module:module")
project(":socialismus-module:module").projectDir = file("socialismus-module/module")

include(":socialismus-module:module-common")
project(":socialismus-module:module-common").projectDir = file("socialismus-module/module-common")

include(":modules")
project(":modules").projectDir = file("modules")

include(":module-chirper-api")
project(":module-chirper-api").projectDir = file("modules/module-chirper/chirper-api")

include(":module-chirper-common")
project(":module-chirper-common").projectDir = file("modules/module-chirper/chirper-common")

include(":module-chirper-command")
project(":module-chirper-command").projectDir = file("modules/module-chirper/chirper-command")

include(":module-chirper")
project(":module-chirper").projectDir = file("modules/module-chirper/chirper")

include(":module-bubbler-api")
project(":module-bubbler-api").projectDir = file("modules/module-bubbler/bubbler-api")

include(":module-bubbler-common")
project(":module-bubbler-common").projectDir = file("modules/module-bubbler/bubbler-common")

include(":module-bubbler-command")
project(":module-bubbler-command").projectDir = file("modules/module-bubbler/bubbler-command")

include(":module-bubbler")
project(":module-bubbler").projectDir = file("modules/module-bubbler/bubbler")

include(":module-essentials-api")
project(":module-essentials-api").projectDir = file("modules/module-essentials/essentials-api")

include(":module-essentials-common")
project(":module-essentials-common").projectDir = file("modules/module-essentials/essentials-common")

include(":module-essentials-command")
project(":module-essentials-command").projectDir = file("modules/module-essentials/essentials-command")

include(":module-essentials-feature")
project(":module-essentials-feature").projectDir = file("modules/module-essentials/essentials-feature")

include(":module-essentials")
project(":module-essentials").projectDir = file("modules/module-essentials/essentials")

include(":module-dialogue-api")
project(":module-dialogue-api").projectDir = file("modules/module-dialogue/dialogue-api")

include(":module-dialogue-common")
project(":module-dialogue-common").projectDir = file("modules/module-dialogue/dialogue-common")

include(":module-dialogue-command")
project(":module-dialogue-command").projectDir = file("modules/module-dialogue/dialogue-command")

include(":module-dialogue")
project(":module-dialogue").projectDir = file("modules/module-dialogue/dialogue")

include(":module-database-api")
project(":module-database-api").projectDir = file("modules/module-database/database-api")

include(":module-database-common")
project(":module-database-common").projectDir = file("modules/module-database/database-common")

include(":module-database")
project(":module-database").projectDir = file("modules/module-database/database")

include(":module-network-api")
project(":module-network-api").projectDir = file("modules/module-network/network-api")

include(":module-network-common")
project(":module-network-common").projectDir = file("modules/module-network/network-common")

include(":module-network")
project(":module-network").projectDir = file("modules/module-network/network")

include(":module-redis-api")
project(":module-redis-api").projectDir = file("modules/module-redis/redis-api")

include(":module-redis-common")
project(":module-redis-common").projectDir = file("modules/module-redis/redis-common")

include(":module-redis")
project(":module-redis").projectDir = file("modules/module-redis/redis")

include(":module-channelizer-api")
project(":module-channelizer-api").projectDir = file("modules/module-channelizer/channelizer-api")

include(":module-channelizer-common")
project(":module-channelizer-common").projectDir = file("modules/module-channelizer/channelizer-common")

include(":module-channelizer-platform-bukkit")
project(":module-channelizer-platform-bukkit").projectDir = file("modules/module-channelizer/channelizer-platform/platform-bukkit")

include(":module-channelizer")
project(":module-channelizer").projectDir = file("modules/module-channelizer/channelizer")

include(":integration-bstats")
project(":integration-bstats").projectDir = file("socialismus-integration/integration-bstats")

include(":integration-packetevents")
project(":integration-packetevents").projectDir = file("socialismus-integration/integration-packetevents")

include(":integration-papiproxybridge")
project(":integration-papiproxybridge").projectDir = file("socialismus-integration/integration-papiproxybridge")

include(":integration-placeholderapi")
project(":integration-placeholderapi").projectDir = file("socialismus-integration/integration-placeholderapi")

include(":platform-proxy-velocity")
project(":platform-proxy-velocity").projectDir = file("socialismus-platform/type/platform-proxy/velocity")

include(":platform-bukkit-common")
project(":platform-bukkit-common").projectDir = file("socialismus-platform/type/platform-bukkit/common")

include(":platform-bukkit-bukkit")
project(":platform-bukkit-bukkit").projectDir = file("socialismus-platform/type/platform-bukkit/bukkit")

include(":platform-bukkit-paper")
project(":platform-bukkit-paper").projectDir = file("socialismus-platform/type/platform-bukkit/paper")
