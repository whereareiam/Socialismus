plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("Essentials")
}

dependencies {
    implementation(projects.moduleEssentialsApi)
    implementation(projects.moduleEssentialsCommand)
    implementation(projects.moduleEssentialsCommon)
    implementation(projects.moduleEssentialsFeature)
}
