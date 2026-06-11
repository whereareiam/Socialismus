plugins {
    id("shared")
    alias(libs.plugins.attache)
}

dependencies {
    compileOnly(project(":socialismus-module:module-api"))

    attache(libs.cloud.core)
    attache(libs.cloud.annotations)
    attache(libs.cloud.cooldowns)
    attache(libs.cloud.minecraft.extras)
    attache(libs.commandant.common)
}
