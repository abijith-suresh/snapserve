pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "backend"
include(
    "admin-service", "auth-service", "booking-service", "complaint-service",
    "config-service", "customer-service", "gateway-service", "notification-service",
    "registry-service", "review-service", "specialist-service"
)
