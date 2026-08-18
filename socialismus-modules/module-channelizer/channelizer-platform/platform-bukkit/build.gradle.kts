plugins {
    id("shared")
}

dependencies {
    compileOnly(libs.spigot)
    testImplementation(libs.spigot)
    compileOnly(project(":module-channelizer-api"))
    testImplementation(project(":module-channelizer-api"))
}
