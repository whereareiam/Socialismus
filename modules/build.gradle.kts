plugins {
    base
}

tasks.register("moduleArtifacts") {
    group = "build"
    description = "Builds the jars of all official Socialismus modules."

    dependsOn(
        ":module-chirper:build",
        ":module-bubbler:build",
        ":module-dialogue:build",
        ":module-essentials:build",
        ":module-redis:build",
        ":module-channelizer:build"
    )
}
