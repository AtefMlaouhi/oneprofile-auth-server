package com.newlight77.oauth2.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.Filter;

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private Filter ssoFilter;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
      throws Exception {
    // use this because spring security.user.password is deprecatee
    PasswordEncoder encoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder();
    auth
        .inMemoryAuthentication()
          .withUser("franck")
          .password(encoder.encode("password"))
          .roles("TRUSTED_CLIENT")
        .and()
          .withUser("francois")
          .password(encoder.encode("password"))
          .roles("TRUSTED_CLIENT")
        .and()
          .withUser("fabien")
          .password(encoder.encode("password"))
          .roles("USER")
        .and()
          .withUser("benoit")
          .password(encoder.encode("password"))
          .roles("USER")
        .and()
          .withUser("kong")
          .password(encoder.encode("password"))
          .roles("ADMIN", "USER", "TRUSTED_CLIENT")
        .and()
          .withUser("admin")
          .password("{noop}password")
          .roles("ADMIN")
        .and()
          .withUser("user")
          .password(encoder.encode("password"))
          .roles("USER")

    ;
  }

  @Override
  public void configure(WebSecurity security) throws Exception {
    security.ignoring()
        .mvcMatchers(
            "/favicon.ico",
            "/webjars/**",
            "/css/**",
            "/error/**"
            );
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off

    http
        .formLogin()
          .permitAll()
          .loginPage("/login")
          .failureUrl("/error/error")
          .defaultSuccessUrl("/")
        .and()
          .antMatcher("/**")
          .requestMatchers()
          .antMatchers(
              "/user",
              "/login/**",
              "/login",
              "/logout",
              "/oauth/authorize",
              "/oauth/confirm_access",
              "/oauth/error"
          )
        .and()
          .authorizeRequests()
          .antMatchers(
              "/",
              "/login/**",
              "/login",
              "/logout",
              "/oauth/token",
              "/oauth/error"
          )
          .permitAll()
          .anyRequest()
          .authenticated()
        .and()
          .exceptionHandling()
          .accessDeniedPage("/error/access_denied")
          .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        .and()
          .csrf()
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
          .addFilterBefore(ssoFilter, BasicAuthenticationFilter.class)
        ;

    // @formatter:on
  }

}
