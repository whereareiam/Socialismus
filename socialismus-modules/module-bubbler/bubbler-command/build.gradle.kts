plugins {
    id("shared")
}

dependencies {
    compileOnly(libs.commandant)
    compileOnly(libs.cloud.annotations)
    compileOnly(libs.packetevents)
    compileOnly(project(":module-bubbler-api"))
    testImplementation(project(":module-bubbler-api"))
}
