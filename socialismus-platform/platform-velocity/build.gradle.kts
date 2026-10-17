plugins {
    id("packaging-platform-jar")
    alias(libs.plugins.attache)
}

platform {
    name.set("VELOCITY")
}

dependencies {
    compileOnly(projects.socialismusModule.moduleApi)
    implementation(projects.integrationPapiproxybridge)

    compileOnly(libs.bundles.velocity)
    annotationProcessor(libs.velocity)
    implementation(libs.attache.velocity)
    implementation(libs.bundles.bStats.velocity)

    attache(libs.cloud.velocity)
}
