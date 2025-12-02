repositories {
    maven("https://jitpack.io")
}

dependencies {
    "compileOnly"(project(":socialismus-api"))

    "compileOnly"(libs.cloud.core)
    "compileOnly"(libs.cloud.annotations)
    "compileOnly"(libs.cloud.cooldowns)
    "compileOnly"(libs.cloud.minecraft.extras)
}