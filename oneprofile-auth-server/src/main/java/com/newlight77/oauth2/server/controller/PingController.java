package com.newlight77.oauth2.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/ping") public class PingController {

  @RequestMapping(value = "/", method = RequestMethod.GET) 
  public String home() {
    return "ping ok";
  }

}
