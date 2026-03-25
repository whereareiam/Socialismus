plugins {
    id("whereami.convention.basic")
    alias(libs.plugins.shadow)
    id("whereami.convention.shadow")
}

dependencies {
    compileOnly(libs.bundles.cloud)
    compileOnly(libs.spigot)
    compileOnly(projects.socialismusApi)

    implementation(projects.socialismusIntegration.integrationBstats)
    implementation(projects.socialismusIntegration.integrationPacketevents)
    implementation(rootProject.libs.bundles.bStats.bukkit)
}

