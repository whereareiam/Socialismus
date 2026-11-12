plugins {
    id("packaging-bundle-jar")
    id("packaging-downgraded-jar")
}

downgrade {
    // Its Paper side runs from Paper 1.20.6, which runs on Java 21; Velocity 4 runs on Java 25 anyway.
    java.set(21)
}
