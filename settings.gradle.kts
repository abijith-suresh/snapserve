rootProject.name = "snapserve"

include(
    ":backend:api-gateway",
    ":backend:auth-service",
    ":backend:user-service",
    ":backend:booking-service",
    ":backend:notification-service"
)
