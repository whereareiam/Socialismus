plugins {
    id("packaging-platform-jar")
    alias(libs.plugins.attache)
}

platform {
    name.set("VELOCITY")
    descriptors.add("velocity-plugin.json")
}

// The Velocity API of 4.x is built for Java 25.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    compileOnly(projects.socialismusModule.moduleApi)
    implementation(projects.integrationPapiproxybridge)

    compileOnly(libs.bundles.velocity)
    annotationProcessor(libs.velocity)
    implementation(libs.attache.velocity)
    implementation(libs.chassis.platform.velocity)
    implementation(libs.bundles.bStats.velocity)

    attache(libs.cloud.velocity)
}
