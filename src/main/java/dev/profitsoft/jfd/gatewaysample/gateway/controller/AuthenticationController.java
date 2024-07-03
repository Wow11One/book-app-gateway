package dev.profitsoft.jfd.gatewaysample.gateway.controller;

import dev.profitsoft.jfd.gatewaysample.gateway.auth.dto.GoogleOauthAuthenticationAddress;
import dev.profitsoft.jfd.gatewaysample.gateway.filter.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

  AuthenticationService authenticationService;

  @GetMapping("google/authenticate")
  public Mono<GoogleOauthAuthenticationAddress> authenticate(ServerWebExchange exchange) {
    return authenticationService.authenticate(exchange);
  }

  @GetMapping("google/callback")
  public Mono<Void> callback(ServerWebExchange exchange) {
    return authenticationService.authCallback(exchange);
  }

}
