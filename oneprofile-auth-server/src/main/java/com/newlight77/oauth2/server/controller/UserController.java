package com.newlight77.oauth2.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController @RequestMapping("/user") public class UserController {

  @RequestMapping("")
  public Principal user(Principal user) {
    return user;
  }

}
