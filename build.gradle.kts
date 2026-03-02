subprojects {
    apply(plugin = "java")

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
}
