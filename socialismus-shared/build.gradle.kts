tasks.register<Copy>("processSources") {
    from("src/main/java")
    into(layout.buildDirectory.dir("processed-src").get().asFile)
    include("**/*.java")
    filter { line ->
        line.replace("@name@", rootProject.name)
            .replace("@version@", rootProject.version.toString().uppercase())
            .replace("@guiceVersion@", rootProject.libs.versions.guice.get())
            .replace("@jacksonVersion@", rootProject.libs.versions.jackson.get())
            .replace("@snakeyamlVersion@", rootProject.libs.versions.snakeyaml.get())
            .replace("@adventureVersion@", rootProject.libs.versions.adventure.minimessage.get())
            .replace("@adventureBukkitVersion@", rootProject.libs.versions.adventure.platform.bukkit.get())
            .replace("@cloudVersion@", rootProject.libs.versions.cloud.core.get())
            .replace("@cloudCooldownVersion@", rootProject.libs.versions.cloud.cooldown.get())
            .replace("@cloudPaperVersion@", rootProject.libs.versions.cloud.paper.get())
            .replace("@cloudVelocityVersion@", rootProject.libs.versions.cloud.velocity.get())
            .replace("@cloudMinecraftExtrasVersion@", rootProject.libs.versions.cloud.minecraft.extras.get())
    }
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn("processSources")
    source = fileTree(layout.buildDirectory.dir("processed-src").get().asFile)
}