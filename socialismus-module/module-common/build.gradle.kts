plugins {
    id("module-java")
}

dependencies {
    implementation(projects.socialismusModule.moduleApi)
    implementation(libs.chassis.common)
    // Each module's own libraries load through Chassis's Attache adapter, into its class loader.
    implementation(libs.chassis.adapter.attache)
    implementation(libs.attache.standalone)
}
