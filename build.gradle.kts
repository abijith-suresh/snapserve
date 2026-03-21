plugins {
    alias(libs.plugins.spotless) apply false
}

subprojects {
    // :backend is a virtual parent directory project with no source — skip it
    if (childProjects.isEmpty()) {
        apply(plugin = "java")

        extensions.configure<JavaPluginExtension> {
            toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
        }

        repositories {
            mavenCentral()
        }

        dependencies {
            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }

        apply(plugin = "com.diffplug.spotless")
        extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            java {
                googleJavaFormat()
                removeUnusedImports()
                trimTrailingWhitespace()
                endWithNewline()
            }
        }
    }
}
