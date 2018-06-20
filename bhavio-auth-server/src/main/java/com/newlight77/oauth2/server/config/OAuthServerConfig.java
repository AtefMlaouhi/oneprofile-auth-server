package com.newlight77.oauth2.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.PostConstruct;

@Configuration
@EnableAuthorizationServer
public class OAuthServerConfig extends AuthorizationServerConfigurerAdapter {

  private final Logger LOGGER = LoggerFactory.getLogger(OAuthServerConfig.class);

  private final AuthenticationManager authenticationManager;

  public OAuthServerConfig(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Autowired
  private AuthorizationEndpoint authorizationEndpoint;

  @PostConstruct
  public void init() {
    authorizationEndpoint.setUserApprovalPage("forward:/oauth/confirm_access");
    authorizationEndpoint.setErrorPage("forward:/oauth/error");
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    // This can be used to configure security of your authorization server itself
    // i.e. which user can generate tokens , changing default realm etc - Sample code below.

    // we're allowing access to the token only for clients with  'ROLE_TRUSTED_CLIENT' authority.
    // There are few more configurations and changing default realm is one of those

    oauthServer
//        .allowFormAuthenticationForClients()
        .tokenKeyAccess("permitAll()")
        .checkTokenAccess("isAuthenticated()")
//				.tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
//				.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
    ;

  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    // Here you will specify about `ClientDetailsService` i.e.
    // information about OAuth2 clients & where their info is located - memory , DB , LDAP etc.
    // Sample code below

    clients
      .inMemory()

        .withClient("adminclient")
        .secret("{noop}adminclientsecret")
        .authorizedGrantTypes(
            // authorization_code: The client application is strongly authenticated because it has to send all its
            // credentials (client_id+ client_secret + redirect_uri) before it can get a token
            "authorization_code",
            "client_credentials",
            "refresh_token")
        .authorities("ROLE_USER", "ROLE_ADMIN")
        .scopes("read", "write", "admin")
        .redirectUris("http://localhost:8080/sso/login")
//        .resourceIds("oauth2_resource_id")
        .accessTokenValiditySeconds(3600) // 1 hour
        .refreshTokenValiditySeconds(2592000) // 30 days
        .autoApprove(true)

      .and()
        .withClient("publicclient")
        .secret("{noop}publicclientsecret")
        .authorizedGrantTypes(
            // implicit: almost the same as authorization_code,
            // but for public clients (web apps or installed/mobile applications)
            "implicit",
            "client_credentials",
            "refresh_token")
        .authorities("ROLE_TRUSTED_CLIENT")
        .scopes("read", "write", "external_services")
        .redirectUris("http://localhost:8080/sso/login")
        //        .resourceIds("oauth2_resource_id")
        .accessTokenValiditySeconds(3600) // 1 hour
        .refreshTokenValiditySeconds(2592000) // 30 days
        .autoApprove(true)

      .and()
        .withClient("internalresourceserver")
        .secret("{noop}internalresourceserversecret")
        .authorizedGrantTypes(
            "authorization_code",
            "client_credentials",
            "refresh_token",
            // password: the client application collects the user credentials, and sends both the user credentials
            // (username+password) and its own credentials (client_id+client_secret) in exchange for a token
            "password")
        .authorities("ROLE_USER")
        .scopes("read", "write", "internal_services")
        .redirectUris("http://localhost:8080/sso/login")
        //        .resourceIds("oauth2_resource_id")
        .accessTokenValiditySeconds(3600) // 1 hour
        .refreshTokenValiditySeconds(2592000) // 30 days
        .autoApprove(true)

      .and()
        .withClient("acme")
        .secret("{noop}acmesecret")
        .authorizedGrantTypes(
            "authorization_code",
            "client_credentials",
            "refresh_token",
            "password")
        .authorities("ROLE_USER", "ROLE_ADMIN", "ROLE_TRUSTED_CLIENT")
        .scopes("read", "write", "internal_services", "external_services", "admin")
        .redirectUris("http://localhost:8080/sso/login")
        //        .resourceIds("oauth2_resource_id")
        .accessTokenValiditySeconds(3600) // 1 hour
        .refreshTokenValiditySeconds(2592000) // 30 days
        .autoApprove(true)

    ;

  }

  @Override public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    // Here you will do non-security configs for end points associated with your Authorization Server
    // and can specify details about authentication manager, token generation etc. Sample code below
    endpoints.authenticationManager(this.authenticationManager)
//        .tokenServices(tokenServices())
        .tokenStore(tokenStore())
        .accessTokenConverter(accessTokenConverter())
    ;
  }

  @Bean
  @Primary
  public DefaultTokenServices tokenServices() {
    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(tokenStore());
    defaultTokenServices.setSupportRefreshToken(true);
    return defaultTokenServices;
  }

  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(accessTokenConverter());
  }


  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    final Resource resource = new ClassPathResource("keystore.jks");
    final KeyStoreKeyFactory keyStoreKeyFactory =
        new KeyStoreKeyFactory(resource, "testpass".toCharArray());
    converter.setKeyPair(keyStoreKeyFactory.getKeyPair("oauth"));
    return converter;
  }

}
