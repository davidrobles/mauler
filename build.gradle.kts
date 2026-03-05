plugins {
    id("com.diffplug.spotless") version "6.25.0" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")

    repositories {
        mavenCentral()
    }

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }

    tasks.withType<Test> {
        useJUnit()
    }

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            // AOSP style: 4-space indentation (matches existing codebase)
            googleJavaFormat("1.15.0").aosp()
        }
    }
}
