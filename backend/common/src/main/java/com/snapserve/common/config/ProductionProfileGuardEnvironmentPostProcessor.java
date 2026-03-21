package com.snapserve.common.config;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

public class ProductionProfileGuardEnvironmentPostProcessor
    implements EnvironmentPostProcessor, Ordered {

  private static final String ACTIVE_PROFILES_PROPERTY = "spring.profiles.active";
  private static final String APP_ENV_PROPERTY = "APP_ENV";
  private static final String REQUIRED_PROFILE = "prod";

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    String appEnv = environment.getProperty(APP_ENV_PROPERTY, "").trim();
    if (!isProductionEnvironment(appEnv)) {
      return;
    }

    boolean prodProfileActive =
        environment.getActiveProfiles().length == 0
            ? containsProd(environment.getProperty(ACTIVE_PROFILES_PROPERTY, ""))
            : containsProd(environment.getActiveProfiles());

    if (!prodProfileActive) {
      throw new IllegalStateException(
          "APP_ENV=production requires spring profile 'prod' to be active");
    }
  }

  private boolean isProductionEnvironment(String appEnv) {
    String normalized = appEnv.toLowerCase(Locale.ROOT);
    return "prod".equals(normalized) || "production".equals(normalized);
  }

  private boolean containsProd(String... profiles) {
    for (String profile : profiles) {
      if (profile == null) {
        continue;
      }

      for (String candidate : profile.split(",")) {
        if (REQUIRED_PROFILE.equals(candidate.trim())) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
