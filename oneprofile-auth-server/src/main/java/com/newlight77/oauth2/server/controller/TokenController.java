package com.newlight77.oauth2.server.controller;

import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class TokenController {


  @Resource(name = "tokenServices")
  private ConsumerTokenServices tokenServices;

  @RequestMapping(method = RequestMethod.DELETE, value = "/oauth/token/{tokenId}")
  @ResponseBody
  public void revokeToken(@PathVariable String tokenId) {
    tokenServices.revokeToken(tokenId);
  }

}
