package com.newlight77.oauth2.server.controller;

import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
public class TokenController {

  @Resource(name = "tokenServices")
  private ConsumerTokenServices tokenServices;

  @DeleteMapping("/oauth/token/")
  @ResponseBody
  public void revokeToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (authorization != null && authorization.contains("Bearer")) {
      String tokenId = authorization.substring("Bearer".length() + 1);
      tokenServices.revokeToken(tokenId);
    }
  }

  @DeleteMapping("/oauth/token/{tokenId}")
  @ResponseBody
  public void revokeToken(@PathVariable String tokenId) {
    tokenServices.revokeToken(tokenId);
  }

}
