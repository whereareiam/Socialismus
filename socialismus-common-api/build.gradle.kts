plugins {
    id("maven-publish")
}

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    "compileOnly"(libs.bundles.adventure)
    "compileOnly"(libs.libby.core)
    "compileOnly"(libs.ormlite)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "me.whereareiam"
            artifactId = rootProject.name
            version = rootProject.version.toString()

            from(components["java"])
        }
    }
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
        title = "Socialismus API"
        windowTitle = "Socialismus API"
    }
}

tasks.register<Copy>("processSources") {
    from("src/main/java")
    into(layout.buildDirectory.dir("processed-src").get().asFile)
    include("**/*.java")
    filter { line ->
        line.replace("@name@", rootProject.name)
            .replace("@version@", rootProject.version.toString().uppercase())
            .replace("@guiceVersion@", rootProject.libs.versions.guice.get())
            .replace("@configuraVersion@", rootProject.libs.versions.configura.get())
            .replace("@jedisVersion@", rootProject.libs.versions.jedis.get())
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
