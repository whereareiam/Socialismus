plugins {
    id("packaging-module-jar")
}

// A module for the module journeys: it depends on JourneyLibrary and on the PacketEvents plugin.
socialismusModule {
    name.set("JourneyConsumer")
}

dependencies {
    compileOnly(projects.fixtureModuleLibrary)
}
