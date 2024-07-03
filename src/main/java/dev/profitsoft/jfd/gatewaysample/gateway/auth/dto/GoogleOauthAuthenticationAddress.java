package dev.profitsoft.jfd.gatewaysample.gateway.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Jacksonized
@Builder
public class GoogleOauthAuthenticationAddress {

  String address;
}
