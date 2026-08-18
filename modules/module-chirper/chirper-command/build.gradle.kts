plugins {
    id("shared")
}

dependencies {
    compileOnly(libs.commandant)
    compileOnly(libs.cloud.annotations)
    compileOnly(project(":module-chirper-api"))
    testImplementation(project(":module-chirper-api"))
}
