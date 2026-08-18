plugins {
    base
}

tasks.register("redisModules") {
    group = "build"
    description = "Builds all SocialismusRedis module artifacts."

    dependsOn(
        ":module-redis-api:build",
        ":module-redis-common:build",
        ":module-redis-runtime:build"
    )
}
