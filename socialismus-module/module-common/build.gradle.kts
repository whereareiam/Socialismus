plugins {
    id("module-java")
}

dependencies {
    implementation(projects.socialismusModule.moduleApi)
    // Each module's own libraries load through a library manager of its own, into its class loader.
    implementation(libs.attache.standalone)
}
