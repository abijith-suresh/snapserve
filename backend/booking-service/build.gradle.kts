// TODO: WebFlux→MVC refactor tracked in issue #19.
// Deps below match the existing reactive code to keep CI green.
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
    implementation(libs.spring.boot.starter.web)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation(libs.spring.boot.starter.actuator)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
}
