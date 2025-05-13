import org.apache.tools.ant.filters.ReplaceTokens

subprojects {
    if (project.name != "common") {
        dependencies {
            "implementation"(project(":socialismus-platform:platform-bukkit:common"))
            "implementation"(project(":socialismus-integration:integration-placeholderapi"))

            "compileOnly"(rootProject.libs.cloud.paper)
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
}