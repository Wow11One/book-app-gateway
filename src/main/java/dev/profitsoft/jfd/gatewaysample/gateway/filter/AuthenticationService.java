package dev.profitsoft.jfd.gatewaysample.gateway.filter;

import dev.profitsoft.jfd.gatewaysample.gateway.auth.GoogleAuthenticationService;
import dev.profitsoft.jfd.gatewaysample.gateway.auth.dto.GoogleOauthAuthenticationAddress;
import dev.profitsoft.jfd.gatewaysample.gateway.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService{

  private static final String PREFIX_OAUTH = "/oauth";
  private static final String ENDPOINT_CALLBACK = PREFIX_OAUTH + "/google/callback";
  public static final String COOKIE_AUTH_STATE = "auth-state";
  public static final String COOKIE_SESSION_ID = "SESSION-ID";

  private final GoogleAuthenticationService googleAuthenticationService;

  private final SessionService sessionService;

  public Mono<GoogleOauthAuthenticationAddress> authenticate(ServerWebExchange exchange) {
    String state = UUID.randomUUID().toString();
    addStateCookie(exchange, state);
    String redirectUri = buildRedirectUri(exchange.getRequest());
    String authenticationUrl = googleAuthenticationService.generateAuthenticationUrl(redirectUri, state);

    return Mono.just(GoogleOauthAuthenticationAddress.builder().address(authenticationUrl).build());
  }

  public Mono<Void> authCallback(ServerWebExchange exchange) {
    String code = exchange.getRequest().getQueryParams().getFirst("code");
    String redirectUri = buildRedirectUri(exchange.getRequest());
    return googleAuthenticationService.processAuthenticationCallback(code, redirectUri)
      .doOnNext(userInfo -> log.info("User authenticated: {}", userInfo))
      .flatMap(sessionService::saveSession)
      .flatMap(session -> sessionService.addSessionCookie(exchange, session))
      .doOnError(error -> log.error("error occurred while parsing session id code: {}", error.getMessage()))
      .then(sendRedirect(exchange, "http://localhost:3050"));
  }

  private static void addStateCookie(ServerWebExchange exchange, String state) {
    exchange.getResponse().addCookie(ResponseCookie.from(COOKIE_AUTH_STATE)
        .value(state)
        .path(PREFIX_OAUTH)
        .maxAge(Duration.of(30, ChronoUnit.MINUTES))
        .build());
  }

  private static Mono<Void> sendRedirect(ServerWebExchange exchange, String location) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.FOUND);
    response.getHeaders().add("Location", location);
    return response.setComplete();
  }

  private String buildRedirectUri(ServerHttpRequest request) {
    String baseUrl = getBaseUrl(request);
    return baseUrl + ENDPOINT_CALLBACK;
  }

  private static String getBaseUrl(ServerHttpRequest request) {
    return request.getURI().toString().substring(0, request.getURI().toString().indexOf(PREFIX_OAUTH));
  }

}
