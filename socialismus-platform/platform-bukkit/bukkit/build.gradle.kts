import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks.withType<ShadowJar> {
    archiveClassifier.set("BUKKIT")

    relocate("net.kyori.adventure", "me.whereareiam.socialismus.library.adventure")
}

dependencies {
    "compileOnly"(libs.bundles.bukkit)
    "implementation"(libs.libby.bukkit)
}