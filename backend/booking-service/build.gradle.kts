plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dep.mgmt)
    alias(libs.plugins.spotless)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.spring.cloud.get()}")
    }
}

dependencies {
    implementation(project(":backend:common"))
    implementation(project(":backend:user-service-client"))
    implementation(project(":backend:notification-service-client"))
    implementation(libs.spring.cloud.openfeign)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)

    // Lombok (must be before MapStruct)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok.mapstruct.binding)

    // MapStruct for DTO mapping
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    // OpenAPI documentation
    implementation(libs.springdoc.openapi)
    testImplementation(libs.spring.boot.starter.test)
}
