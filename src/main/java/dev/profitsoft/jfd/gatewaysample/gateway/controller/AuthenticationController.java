package dev.profitsoft.jfd.gatewaysample.gateway.controller;

import dev.profitsoft.jfd.gatewaysample.gateway.filter.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationController {

  AuthenticationService authenticationService;

  @GetMapping("google/authenticate")
  public Mono<Void> authenticate(ServerWebExchange exchange) {
    return authenticationService.authenticate(exchange);
  }

}
