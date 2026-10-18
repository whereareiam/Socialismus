plugins {
    id("module-api")
}

group = "me.whereareiam.socialismus.module"

toolkitPublish {
    artifactId.set("Dialogue")

    pom {
        name.set("Dialogue")
        description.set("Public API for Dialogue - Socialismus private messages module")
    }

    javadoc {
        title.set("Dialogue API")
        windowTitle.set("Dialogue API")
    }
}
