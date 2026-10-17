plugins {
    id("module-java")
}

dependencies {
    compileOnly(libs.spigot)
    testImplementation(libs.spigot)
    compileOnly(projects.moduleChannelizerApi)
    testImplementation(projects.moduleChannelizerApi)
}
