plugins {
    id("packaging-module-jar")
}

// A module for the module journeys: it exports its api package and keeps the rest to itself.
socialismusModule {
    name.set("JourneyLibrary")
    exports.add("me.whereareiam.socialismus.testing.fixture.library.api")
}
