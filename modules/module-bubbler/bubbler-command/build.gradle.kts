plugins {
    id("module-java")
}

dependencies {
    compileOnly(libs.commandant)
    compileOnly(libs.cloud.annotations)
    compileOnly(libs.packetevents)
    compileOnly(projects.moduleBubblerApi)
    testImplementation(projects.moduleBubblerApi)
}
