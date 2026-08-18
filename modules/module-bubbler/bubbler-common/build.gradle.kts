plugins {
    id("shared")
}

dependencies {
    compileOnly(libs.packetevents)
    compileOnly(project(":module-bubbler-api"))
    testImplementation(project(":module-bubbler-api"))
}
