package com.newlight77.oauth2.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SessionAttributes("authorizationRequest")
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("login");
    registry.addViewController("/error/access_denied").setViewName("error/access_denied");
    registry.addViewController("/error/error").setViewName("error/error");
    registry.addViewController("/oauth/error").setViewName("oauth/error");
    registry.addViewController("/oauth/confirm_access").setViewName("oauth/confirm_access");
  }
}
