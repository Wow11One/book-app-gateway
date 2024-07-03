package dev.profitsoft.jfd.gatewaysample.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Slf4j
public class GatewayConfig {

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder.routes()
      .route(r -> r.path(
          "/api/books/**",
          "/api/genres/**",
          "/api/authors/**"
        )
        .filters(filter -> filter.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE")
          .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_UNIQUE")
        )
        .uri("lb://book-service")
      )
      .build();
  }

  @Bean
  public CorsWebFilter corsFilter() {
    return new CorsWebFilter(corsConfigurationSource());
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
    config.addAllowedMethod(HttpMethod.PUT);
    config.addAllowedMethod(HttpMethod.DELETE);
    config.addAllowedMethod(HttpMethod.GET);
    config.addAllowedMethod(HttpMethod.OPTIONS);
    config.addAllowedMethod(HttpMethod.POST);
    config.setAllowedOrigins(List.of("http://localhost:3050"));
    config.setAllowCredentials(true);

    log.info("Allowed Origin {}", "http://localhost:3050");

    source.registerCorsConfiguration("/**", config);
    return source;
  }
}

