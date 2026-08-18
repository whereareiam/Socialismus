plugins {
    id("shared")
}

dependencies {
    compileOnly(rootProject.libs.commandant)
    compileOnly(rootProject.libs.cloud.annotations)
    compileOnly(project(":module-essentials-api"))
    testImplementation(project(":module-essentials-api"))
}
