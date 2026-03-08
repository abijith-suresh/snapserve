rootProject.name = "snapserve"

include(
    ":backend:api-gateway",
    ":backend:auth-service",
    ":backend:user-service",
    ":backend:user-service-client",
    ":backend:booking-service",
    ":backend:booking-service-client",
    ":backend:notification-service",
    ":backend:notification-service-client",
    ":backend:common"
)
