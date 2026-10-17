plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("SocialismusChannelizer")
}

dependencies {
    implementation(projects.moduleChannelizerApi)
    implementation(projects.moduleChannelizerCommon)
    implementation(projects.moduleChannelizerPlatformBukkit)
}
