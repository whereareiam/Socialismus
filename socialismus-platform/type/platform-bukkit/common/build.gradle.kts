plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.socialismusModule.moduleApi)
    compileOnly(projects.socialismusPlatform.common)
    implementation(projects.socialismusApi)
    implementation(projects.integrationBstats)

    compileOnly(libs.spigot)
    implementation(libs.bundles.bStats.bukkit)
}
