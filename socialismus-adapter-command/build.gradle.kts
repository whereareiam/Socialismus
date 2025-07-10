repositories {
    maven("https://jitpack.io")
}

dependencies {
    "compileOnly"(project(":socialismus-common-api"))

    "compileOnly"(libs.bundles.cloud)
    "compileOnly"(libs.cloud.paper)
    "compileOnly"(libs.cloud.velocity)
    "annotationProcessor"(libs.cloud.core)
}