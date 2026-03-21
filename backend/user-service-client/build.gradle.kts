plugins {
    `java-library`
    alias(libs.plugins.spring.dep.mgmt)
    alias(libs.plugins.spotless)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.spring.cloud.get()}")
    }
}

dependencies {
    api(project(":backend:common"))
    compileOnly(libs.spring.cloud.openfeign)
    compileOnly(libs.spring.boot.starter.web)
    compileOnly(libs.spring.boot.starter.validation)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
}
