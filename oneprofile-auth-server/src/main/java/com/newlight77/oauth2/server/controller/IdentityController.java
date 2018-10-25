package com.newlight77.oauth2.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class IdentityController {

  @Autowired
  private TokenStore tokenStore;

  @RequestMapping("/me")
  public Principal user(Principal principal) {
    return principal;
  }

  @RequestMapping("/me/username")
  public String username(Principal principal) {
    return principal.getName();
  }

  @RequestMapping("/me/detail")
  @ResponseBody
  public Object detail(Authentication auth) {
    return auth.getDetails();
  }

}
