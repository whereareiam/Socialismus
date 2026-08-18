plugins {
    id("api")
}

toolkitPublish {
    artifactId.set("Chirper")

    pom {
        name.set("Chirper")
        description.set("Public API for Chirper - Socialismus announcement module")
    }

    javadoc {
        title.set("Chirper API")
        windowTitle.set("Chirper API")
    }
}
