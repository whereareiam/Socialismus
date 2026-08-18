plugins {
    id("api")
}

toolkitPublish {
    artifactId.set("SocialismusChannelizer")

    pom {
        name.set("SocialismusChannelizer")
        description.set("Public API for SocialismusChannelizer - Socialismus channel synchronization module")
    }

    javadoc {
        title.set("SocialismusChannelizer API")
        windowTitle.set("SocialismusChannelizer API")
    }
}
