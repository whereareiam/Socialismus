plugins {
    id("module-java")
    alias(libs.plugins.attache)
}

dependencies {
    compileOnly(projects.socialismusModule.moduleApi)

    attache(libs.cloud.core)
    attache(libs.cloud.annotations)
    attache(libs.cloud.cooldowns)
    attache(libs.cloud.minecraft.extras)
    attache(libs.commandant.common)
}
