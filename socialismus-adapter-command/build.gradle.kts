plugins {
    id("shared")
    alias(libs.plugins.attache)
}

dependencies {
    attache(libs.cloud.core)
    attache(libs.cloud.annotations)
    attache(libs.cloud.cooldowns)
    attache(libs.cloud.minecraft.extras)
    attache(libs.commandant.common)
}
