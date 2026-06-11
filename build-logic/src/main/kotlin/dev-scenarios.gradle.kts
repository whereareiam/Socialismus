import org.gradle.jvm.tasks.Jar

evaluationDependsOn(":platform-velocity")
evaluationDependsOn(":platform-bukkit-paper")

val velocityShadowJar = project(":platform-velocity").tasks.named("shadowJar", Jar::class.java)
val paperShadowJar = project(":platform-bukkit-paper").tasks.named("shadowJar", Jar::class.java)

val spawner = extensions.getByName("spawner")
(spawner.readProperty("serverDir") as DirectoryProperty).set(layout.projectDirectory.dir("dev/server"))
val scenarios = spawner.readProperty("scenarios")

scenarios.registerScenario("normal") { scenario ->
    scenario.addPaper("paper") { paper ->
        paper.setInt("port", 25565)
        paper.setBoolean("onlineMode", false)
        paper.addInstall("plugins", paperShadowJar.flatMap { it.archiveFile })
        paper.addModrinth("lKEzGugV", "PlaceholderAPI.jar", "plugins")
    }
}

scenarios.registerScenario("combined") { scenario ->
    scenario.addVelocity("proxy") { velocity ->
        velocity.setInt("port", 25565)
        velocity.setBoolean("onlineMode", false)
        velocity.setString("forwardingMode", "legacy")
        velocity.setDirectory("rootOverlayDir", "dev/scenarios/combined/velocity")
        velocity.addServer("backend", "127.0.0.1:25566")
        velocity.setTryServers("backend")
        velocity.addInstall("plugins", velocityShadowJar.flatMap { it.archiveFile })
    }

    scenario.addPaper("backend") { paper ->
        paper.setInt("port", 25566)
        paper.setBoolean("onlineMode", false)
        paper.setDirectory("rootOverlayDir", "dev/scenarios/combined/backend")
        paper.addInstall("plugins", paperShadowJar.flatMap { it.archiveFile })
        paper.addModrinth("lKEzGugV", "PlaceholderAPI.jar", "plugins")
    }
}

scenarios.registerScenario("sync") { scenario ->
    scenario.addVelocity("proxy") { velocity ->
        velocity.setInt("port", 25565)
        velocity.setBoolean("onlineMode", false)
        velocity.setString("forwardingMode", "legacy")
        velocity.addServer("backendA", "127.0.0.1:25566")
        velocity.addServer("backendB", "127.0.0.1:25567")
        velocity.setTryServers("backendA", "backendB")
    }

    scenario.addPaper("backendA") { paper ->
        paper.setInt("port", 25566)
        paper.setBoolean("onlineMode", false)
        paper.setDirectory("rootOverlayDir", "dev/scenarios/sync/backendA")
        paper.addInstall("plugins", paperShadowJar.flatMap { it.archiveFile })
        paper.addModrinth("lKEzGugV", "PlaceholderAPI.jar", "plugins")
        paper.addModrinth("q8KT1H5f", "SocialismusChannelizer.jar", "plugins/Socialismus/modules")
    }

    scenario.addPaper("backendB") { paper ->
        paper.setInt("port", 25567)
        paper.setBoolean("onlineMode", false)
        paper.setDirectory("rootOverlayDir", "dev/scenarios/sync/backendB")
        paper.addInstall("plugins", paperShadowJar.flatMap { it.archiveFile })
        paper.addModrinth("lKEzGugV", "PlaceholderAPI.jar", "plugins")
        paper.addModrinth("q8KT1H5f", "SocialismusChannelizer.jar", "plugins/Socialismus/modules")
    }
}

private fun Any.registerScenario(name: String, configure: (Any) -> Unit) {
    javaClass.getMethod("register", String::class.java, Action::class.java)
        .invoke(
            this,
            name,
            object : Action<Any> {
                override fun execute(scenario: Any) = configure(scenario)
            }
        )
}

private fun Any.addVelocity(name: String, configure: (Any) -> Unit) {
    javaClass.getMethod("velocity", String::class.java, Action::class.java)
        .invoke(
            this,
            name,
            object : Action<Any> {
                override fun execute(velocity: Any) = configure(velocity)
            }
        )
}

private fun Any.addPaper(name: String, configure: (Any) -> Unit) {
    javaClass.getMethod("paper", String::class.java, Action::class.java)
        .invoke(
            this,
            name,
            object : Action<Any> {
                override fun execute(paper: Any) = configure(paper)
            }
        )
}

@Suppress("UNCHECKED_CAST")
private fun Any.addInstall(into: String, source: Any) {
    javaClass.getMethod("install", Action::class.java)
        .invoke(
            this,
            object : Action<Any> {
                override fun execute(install: Any) {
                    install.javaClass.getMethod("from", Array<Any>::class.java).invoke(install, arrayOf(source))
                    (install.readProperty("into") as Property<String>).set(into)
                }
            }
        )
}

@Suppress("UNCHECKED_CAST")
private fun Any.addModrinth(projectId: String, fileName: String, into: String) {
    javaClass.getMethod("modrinth", String::class.java, Action::class.java)
        .invoke(
            this,
            projectId,
            object : Action<Any> {
                override fun execute(asset: Any) {
                    (asset.readProperty("fileName") as Property<String>).set(fileName)
                    (asset.readProperty("into") as Property<String>).set(into)
                }
            }
        )
}

private fun Any.addServer(name: String, address: String) {
    javaClass.getMethod("server", String::class.java, String::class.java)
        .invoke(this, name, address)
}

private fun Any.setTryServers(vararg names: String) {
    javaClass.getMethod("tryServers", Array<String>::class.java).invoke(this, names)
}

private fun Any.setDirectory(propertyName: String, relativePath: String) {
    (readProperty(propertyName) as DirectoryProperty).set(layout.projectDirectory.dir(relativePath))
}

private fun Any.setInt(propertyName: String, value: Int) {
    @Suppress("UNCHECKED_CAST")
    (readProperty(propertyName) as Property<Int>).set(value)
}

private fun Any.setBoolean(propertyName: String, value: Boolean) {
    @Suppress("UNCHECKED_CAST")
    (readProperty(propertyName) as Property<Boolean>).set(value)
}

@Suppress("UNCHECKED_CAST")
private fun Any.setString(propertyName: String, value: String) {
    (readProperty(propertyName) as Property<String>).set(value)
}

private fun Any.readProperty(name: String): Any {
    val methodName = "get" + name.replaceFirstChar { it.uppercase() }
    return javaClass.getMethod(methodName).invoke(this)
}
