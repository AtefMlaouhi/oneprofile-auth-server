package com.newlight77.oauth2.server;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AccessToken {
  private String accessToken;
  private String refreshToken;
}
