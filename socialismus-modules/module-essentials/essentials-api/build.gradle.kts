plugins {
    id("api")
}

toolkitPublish {
    artifactId.set("Essentials")

    pom {
        name.set("Essentials")
        description.set("Public API for Essentials - Socialismus essentials module")
    }

    javadoc {
        title.set("Essentials API")
        windowTitle.set("Essentials API")
    }
}
