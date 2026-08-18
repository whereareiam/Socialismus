plugins {
    id("shared")
}

dependencies {
    compileOnly(project(":module-chirper-api"))
    testImplementation(project(":module-chirper-api"))
}
