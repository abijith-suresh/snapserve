package com.snapserve.common.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

class ProductionProfileGuardEnvironmentPostProcessorTest {

  private final ProductionProfileGuardEnvironmentPostProcessor processor =
      new ProductionProfileGuardEnvironmentPostProcessor();

  @Test
  void allowsNonProductionEnvironmentWithoutProdProfile() {
    MockEnvironment environment = new MockEnvironment().withProperty("APP_ENV", "local");

    assertThatCode(() -> processor.postProcessEnvironment(environment, new SpringApplication()))
        .doesNotThrowAnyException();
  }

  @Test
  void rejectsProductionEnvironmentWithoutProdProfile() {
    MockEnvironment environment = new MockEnvironment().withProperty("APP_ENV", "production");

    assertThatThrownBy(() -> processor.postProcessEnvironment(environment, new SpringApplication()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("requires spring profile 'prod'");
  }

  @Test
  void allowsProductionEnvironmentWhenProdProfileIsActive() {
    MockEnvironment environment = new MockEnvironment().withProperty("APP_ENV", "production");
    environment.setActiveProfiles("prod");

    assertThatCode(() -> processor.postProcessEnvironment(environment, new SpringApplication()))
        .doesNotThrowAnyException();
  }
}
