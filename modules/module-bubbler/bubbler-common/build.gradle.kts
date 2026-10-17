plugins {
    id("module-java")
}

dependencies {
    compileOnly(libs.packetevents)
    compileOnly(projects.moduleBubblerApi)
    testImplementation(projects.moduleBubblerApi)
}
