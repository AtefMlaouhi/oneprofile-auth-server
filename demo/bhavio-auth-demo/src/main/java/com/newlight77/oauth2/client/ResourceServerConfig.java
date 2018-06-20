package com.newlight77.oauth2.client;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.IOException;
import java.util.Map;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  private final Logger LOGGER = LoggerFactory.getLogger(ResourceServerConfig.class);

  @Override
  public void configure(final ResourceServerSecurityConfigurer config) {
    config.tokenServices(tokenServices());
  }

  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(accessTokenConverter());
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setAccessTokenConverter(customAccessTokenConverter());

    final Resource resource = new ClassPathResource("key.pub");
    String publicKey = null;
    try {
      publicKey = IOUtils.toString(resource.getInputStream());
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    converter.setVerifierKey(publicKey);
    return converter;
  }

  @Bean
  @Primary
  public DefaultTokenServices tokenServices() {
    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(tokenStore());
    return defaultTokenServices;
  }

  private DefaultAccessTokenConverter customAccessTokenConverter() {
    return new DefaultAccessTokenConverter() {
      @Override
      public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
        OAuth2Authentication authentication = super.extractAuthentication(claims);
        authentication.setDetails(claims);
        return authentication;
      }
    };
  }

}

