plugins {
    id("runtime")
}

dependencies {
    compileOnly(project(":socialismus-module:module-api"))
    implementation(projects.socialismusApi)
    implementation(projects.integrationBstats)

    compileOnly(libs.spigot)
    implementation(libs.bundles.bStats.bukkit)
}
