package com.newlight77.oauth2.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@EnableOAuth2Sso
@Order(-100)
public class Oauth2ResourceSsoApplication
    extends WebSecurityConfigurerAdapter
{
  private final Logger LOGGER = LoggerFactory.getLogger(Oauth2ResourceSsoApplication.class);

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Oauth2ResourceSsoApplication.class, args);
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    super.configure(web);

    //@formatter:off
    web.ignoring()
        .mvcMatchers("/favicon.ico", "/webjars/**", "/css/**");
    //@formatter:on
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //@formatter:off
    http
        .antMatcher("/**")
          .requestMatchers()
          .antMatchers(
              "/", // permitAll()
              "/login", // authenticated()
              "/user" // authenticated() = true
          )
        .and()
          .authorizeRequests()
          .antMatchers(
              "/" // permitAll()
          )
          .permitAll()
          .anyRequest()
          .authenticated()
        .and()
          .csrf()
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
          .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
    ;

    //@formatter:on

  }

  private Filter csrfHeaderFilter() {
    return new OncePerRequestFilter() {
      @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
          ServletException,
          IOException {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
          Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
          String token = csrf.getToken();
          if (cookie == null || token != null && !token.equals(cookie.getValue())) {
            cookie = new Cookie("XSRF-TOKEN", token);
            cookie.setPath("/");
            response.addCookie(cookie);
          }
        }
        filterChain.doFilter(request, response);
      }
    };
  }

  private CsrfTokenRepository csrfTokenRepository() {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");
    return repository;
  }


}
