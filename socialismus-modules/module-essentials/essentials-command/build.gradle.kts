plugins {
    id("shared")
}

dependencies {
    compileOnly(libs.commandant)
    compileOnly(libs.cloud.annotations)
    compileOnly(project(":module-essentials-api"))
    testImplementation(project(":module-essentials-api"))
}
