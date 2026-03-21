package com.snapserve.gateway.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.rate-limit")
public class GatewayRateLimitProperties {

  private boolean enabled = false;
  private boolean useForwardedFor = false;
  private int requestsPerWindow = 120;
  private int windowSeconds = 60;
  private List<String> includePaths = List.of("/api/**");
  private List<String> excludePaths = List.of("/actuator/**");

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isUseForwardedFor() {
    return useForwardedFor;
  }

  public void setUseForwardedFor(boolean useForwardedFor) {
    this.useForwardedFor = useForwardedFor;
  }

  public int getRequestsPerWindow() {
    return requestsPerWindow;
  }

  public void setRequestsPerWindow(int requestsPerWindow) {
    this.requestsPerWindow = requestsPerWindow;
  }

  public int getWindowSeconds() {
    return windowSeconds;
  }

  public void setWindowSeconds(int windowSeconds) {
    this.windowSeconds = windowSeconds;
  }

  public List<String> getIncludePaths() {
    return includePaths;
  }

  public void setIncludePaths(List<String> includePaths) {
    this.includePaths = includePaths;
  }

  public List<String> getExcludePaths() {
    return excludePaths;
  }

  public void setExcludePaths(List<String> excludePaths) {
    this.excludePaths = excludePaths;
  }
}
