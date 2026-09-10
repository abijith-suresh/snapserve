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
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi)
    implementation(libs.logstash.logback.encoder)
    implementation(libs.mapstruct)
    compileOnly(libs.lombok.mapstruct.binding)
    compileOnly(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.lombok.mapstruct.binding)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
}
