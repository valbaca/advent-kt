plugins {
    kotlin("jvm") version "1.8.0"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")


    // Eclipse Collections for faster primitive collections and Bag, etc.
    implementation("org.eclipse.collections:eclipse-collections-api:11.1.0")
    implementation("org.eclipse.collections:eclipse-collections:11.1.0")

    // Google Guava for more advanced collections: Table, etc
    implementation("com.google.guava:guava:31.1-jre")

    // Jackson for JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.7.1")

    // Arrow for better Functional Programming / Types
    implementation("io.arrow-kt:arrow-core:1.2.0-RC")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0-RC")
}

kotlin {
    jvmToolchain(17)
}