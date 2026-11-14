plugins {
    id("module-api")
}

group = "me.whereareiam.socialismus.module"

toolkitPublish {
    artifactId.set("Mentionator")

    pom {
        name.set("Mentionator")
        description.set("Public API for Mentionator - Socialismus tags module")
    }

    javadoc {
        title.set("Mentionator API")
        windowTitle.set("Mentionator API")
    }
}
