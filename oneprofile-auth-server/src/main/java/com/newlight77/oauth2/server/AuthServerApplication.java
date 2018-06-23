package com.newlight77.oauth2.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.newlight77.oauth2.server")
public class AuthServerApplication {

  private final Logger LOGGER = LoggerFactory.getLogger(AuthServerApplication.class);

  public static void main(String[] args) throws Exception {
    SpringApplication.run(AuthServerApplication.class, args);
  }
}
