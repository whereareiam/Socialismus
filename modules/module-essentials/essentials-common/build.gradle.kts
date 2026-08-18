plugins {
    id("shared")
}

dependencies {
    compileOnly(project(":module-essentials-api"))
    testImplementation(project(":module-essentials-api"))
}
