plugins {
    // These plugins are declared here to make them available to subprojects.
    // They are not applied to the root project itself.
    id("java-library") // This can be applied to the root if it's a common library project
    id("org.springframework.boot") version "3.3.5" apply false // Apply false to prevent applying to root
    id("io.spring.dependency-management") version "1.1.5" apply false // Apply false to prevent applying to root
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    // Apply Spring Boot and dependency management plugins to individual microservices
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "java")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
