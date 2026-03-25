plugins {
    id("whereami.convention.basic")
}

dependencies {
    compileOnly(libs.cloud.annotations)
    compileOnly(libs.cloud.cooldowns)
    compileOnly(libs.cloud.core)
    compileOnly(libs.cloud.minecraft.extras)
    compileOnly(projects.socialismusApi)
}