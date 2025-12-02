import org.apache.tools.ant.filters.ReplaceTokens

subprojects {
    plugins.apply(rootProject.libs.plugins.shadow.get().pluginId)

    if (project.name != "common") {
        dependencies {
            "implementation"(project(":socialismus-platform:platform-bukkit:common"))
            "implementation"(project(":socialismus-integration:integration-placeholderapi"))

            "compileOnly"(rootProject.libs.cloud.paper)
            "compileOnly"(rootProject.libs.brigadier)
        }

        tasks.named<Copy>("processResources") {
            filter<ReplaceTokens>(
                "tokens" to mapOf(
                    "projectName" to rootProject.name,
                    "projectVersion" to project.version
                )
            )
        }
    }

    repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}