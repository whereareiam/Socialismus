plugins {
    base
}

tasks.register("channelizerModules") {
    group = "build"
    description = "Builds all SocialismusChannelizer module artifacts."

    dependsOn(
        ":module-channelizer-api:build",
        ":module-channelizer-common:build",
        ":module-channelizer-platform-bukkit:build",
        ":module-channelizer-runtime:build"
    )
}
