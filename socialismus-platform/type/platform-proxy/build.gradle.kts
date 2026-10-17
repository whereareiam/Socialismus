plugins {
    base
}

tasks.register("proxyPlatforms") {
    group = "build"
    description = "Builds all proxy platform modules."

    dependsOn(":platform-proxy-velocity:build")
}
