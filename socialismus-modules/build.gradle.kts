plugins {
    base
}

tasks.register("moduleArtifacts") {
    group = "build"
    description = "Builds all migrated Socialismus module artifacts."

    dependsOn(
        ":module-chirper:chirperModules",
        ":module-bubbler:bubblerModules",
        ":module-essentials:essentialsModules",
        ":module-redis:redisModules"
    )
}
