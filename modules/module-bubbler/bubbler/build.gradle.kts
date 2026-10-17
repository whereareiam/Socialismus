plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("Bubbler")
}

dependencies {
    implementation(projects.moduleBubblerApi)
    implementation(projects.moduleBubblerCommand)
    implementation(projects.moduleBubblerCommon)
    compileOnly(libs.packetevents)
}
