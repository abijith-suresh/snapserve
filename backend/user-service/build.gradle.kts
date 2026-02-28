plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dep.mgmt)
}

dependencies {}

// No Java source yet; disable bootJar until user-service is implemented.
tasks.named("bootJar") { enabled = false }
