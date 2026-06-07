plugins {
    id("runtime")
}

dependencies {
    implementation(projects.socialismusApi)
    implementation(projects.integrationBstats)

    compileOnly(libs.spigot)
    implementation(libs.bundles.bStats.bukkit)
}
