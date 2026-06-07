tasks.named<ProcessResources>("processResources").configure {
    filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
        expand(
            mapOf(
                "projectName" to rootProject.name,
                "projectVersion" to project.version
            )
        )
    }
}
