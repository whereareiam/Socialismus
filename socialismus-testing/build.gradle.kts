import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import me.whereareiam.anvil.integration.junit.gradle.AnvilTest
import java.util.zip.ZipFile

plugins {
    java
    alias(libs.plugins.anvil)
    alias(libs.plugins.anvil.junit)
    alias(libs.plugins.anvil.capability.default)
    alias(libs.plugins.anvil.platform.paper)
    alias(libs.plugins.anvil.platform.spigot)
    alias(libs.plugins.anvil.platform.velocity)
}

description = "Live player journeys against packaged Socialismus artifacts"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    // Socialismus's configuration models, to write a server's files through them.
    anvilImplementation(projects.socialismusApi)
    anvilImplementation(libs.annotations)
    anvilImplementation(libs.configura)

    anvilRuntimeOnly(libs.anvil.protocol.mcprotocol)

    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.platform)
}

// Anvil's player workers take Adventure only from their protocol library, and the journeys' classpath includes
// what Socialismus's API exposes to compilation, so Adventure stays out of it.
configurations.matching { it.name.startsWith("anvil") }.configureEach {
    exclude(group = "net.kyori")
}

/**
 * The packaged jar of a plugin project, built before the journeys that install it.
 */
fun pluginJar(project: ProjectDependency): Configuration =
    configurations.detachedConfiguration(dependencies.project(project.path, "shadowRuntimeElements"))
        .apply { isTransitive = false }

/**
 * The jar of a plugin Socialismus integrates with, as its authors publish it.
 */
fun thirdPartyPlugin(plugin: Provider<MinimalExternalModuleDependency>): Configuration =
    configurations.detachedConfiguration(dependencies.create(plugin.get()))
        .apply { isTransitive = false }

/**
 * Journeys that run at once. Each one has its own network, so the default leaves four processors to every
 * network; a machine with fewer than eight runs one journey after another.
 */
val journeyParallelism = providers.gradleProperty("socialismus.journeys.parallelism").map(String::toInt)
    .orElse((Runtime.getRuntime().availableProcessors() / 4).coerceIn(1, 4))

/**
 * Suites the journeys are split into, each a JUnit tag; socialismus-testing/JOURNEYS.md plans what each one holds.
 */
val journeySuites = listOf(
    "chat", "commands", "platform", "network", "sync", "upgrade",
    "modules", "chirper", "dialogue", "essentials", "bubbler"
)

/**
 * Chirper's jar with its module.json changed, for the journeys that expect Socialismus to refuse it.
 */
fun alteredChirper(artifact: String, change: MutableMap<String, Any?>.() -> Unit): TaskProvider<Zip> {
    val chirper = pluginJar(projects.moduleChirper)
    val moduleJson = layout.buildDirectory.file("module-fixtures/$artifact/module.json")
    val alterModuleJson = tasks.register("${artifact}ModuleJson") {
        inputs.files(chirper)
        outputs.file(moduleJson)
        doLast {
            val original = ZipFile(chirper.singleFile).use { jar ->
                jar.getInputStream(jar.getEntry("module.json")).reader().readText()
            }
            @Suppress("UNCHECKED_CAST")
            val module = JsonSlurper().parseText(original) as MutableMap<String, Any?>
            module.change()
            moduleJson.get().asFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(module)))
        }
    }

    return tasks.register<Zip>(artifact) {
        from(chirper.elements.map { jars -> jars.map { zipTree(it) } }) {
            exclude("module.json")
        }
        from(alterModuleJson)
        archiveFileName.set("$artifact.jar")
        destinationDirectory.set(layout.buildDirectory.dir("modules"))
    }
}

val moduleRequiringAnotherVersion = alteredChirper("module-requires-another-version") {
    this["socialismus"] = "1\\.0\\.0"
}

val moduleForAnotherPlatform = alteredChirper("module-for-another-platform") {
    this["platforms"] = listOf(mapOf("type" to "VELOCITY"))
}

anvil {
    acceptEula()
    engine {
        protocolLibrary("mcprotocol")
        // A server sizes its thread pools from the processors it sees; all of them at once stall the machine.
        processors.set(4)
        // Lets the test processes yield to whatever else the machine is doing. Windows has no nice.
        if (!System.getProperty("os.name").lowercase().contains("win")) processPriority.set("low")
    }

    artifact("paper", pluginJar(projects.platformBukkitPaper))
    artifact("bukkit", pluginJar(projects.platformBukkitBukkit))
    artifact("velocity", pluginJar(projects.platformProxyVelocity))
    artifact("bundle", pluginJar(projects.socialismusPlatform.bundle))
    artifact("chirper", pluginJar(projects.moduleChirper))
    artifact("dialogue", pluginJar(projects.moduleDialogue))
    artifact("essentials", pluginJar(projects.moduleEssentials))
    artifact("bubbler", pluginJar(projects.moduleBubbler))
    artifact("redis", pluginJar(projects.moduleRedis))
    artifact("module-requires-another-version", files(moduleRequiringAnotherVersion))
    artifact("module-for-another-platform", files(moduleForAnotherPlatform))
    artifact("module-journey-library", pluginJar(projects.fixtureModuleLibrary))
    artifact("module-journey-consumer", pluginJar(projects.fixtureModuleConsumer))
    artifact("packetevents", thirdPartyPlugin(libs.journey.packetevents))

    // Each suite runs the journeys of its tag in anvil<Suite>Test, such as anvilChatTest.
    suites {
        journeySuites.forEach { suite ->
            register(suite) {
                includeTags(suite)
            }
        }
    }
}

// Left with what no suite claims, anvilTest checks that every journey belongs to exactly one suite.
tasks.named<AnvilTest>("anvilTest") {
    systemProperty("socialismus.journeys.suites", journeySuites.joinToString(","))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<AnvilTest>().configureEach {
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    systemProperty("junit.jupiter.execution.parallel.config.strategy", "fixed")
    systemProperty("junit.jupiter.execution.parallel.config.fixed.parallelism", journeyParallelism.get())
    systemProperty("junit.jupiter.execution.parallel.config.fixed.max-pool-size", journeyParallelism.get())
}
