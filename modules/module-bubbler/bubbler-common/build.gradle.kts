plugins {
    id("module-java")
}

dependencies {
    compileOnly(libs.packetevents)
    compileOnly(libs.spigot)
    compileOnly(projects.moduleBubblerApi)
    testImplementation(projects.moduleBubblerApi)
}
