plugins {
    id("integration")
}

dependencies {
    compileOnly(project(":socialismus-module:module-api"))
    compileOnly(libs.bundles.bStats)
}
