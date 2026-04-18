plugins {
    id("socialismus.java-common")
}

dependencies {
    compileOnly(projects.socialismusApi)

    compileOnly(libs.cloud.core)
    compileOnly(libs.cloud.annotations)
    compileOnly(libs.cloud.cooldowns)
    compileOnly(libs.cloud.minecraft.extras)
}
