import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("PAPER")
}

dependencies {
    "compileOnly"(libs.bundles.paper)
    "implementation"(libs.libby.paper)
}