plugins {
    id("shared")
}

dependencies {
    implementation(project(":module-essentials-dialogue"))
    compileOnly(project(":module-essentials-api"))
    testImplementation(project(":module-essentials-api"))
}
