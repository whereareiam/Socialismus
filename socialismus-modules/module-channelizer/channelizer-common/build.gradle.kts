plugins {
    id("shared")
}

dependencies {
    compileOnly(project(":module-channelizer-api"))
    testImplementation(project(":module-channelizer-api"))
}
