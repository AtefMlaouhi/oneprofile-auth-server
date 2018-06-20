package com.newlight77.oauth2.client;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Oauth2ResourceSsoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuthAccessTokenE2ETest {

  @Autowired
  private JwtTokenStore tokenStore;

  @Test
  @Ignore // Integration test interacting with the real running server
  public void whenRequestionTokenUsingValidBasicAuth_thenSuccess() {
    final String tokenValue = requestAccessToken("acme", "acmesecret" ,"user", "password");
    final OAuth2Authentication auth = tokenStore.readAuthentication(tokenValue);
    assertTrue(auth.isAuthenticated());
  }

  private String requestAccessToken(String clientId, String clientSecret, String username, String password) {

    final Response response = RestAssured.given()
        .auth()
        .preemptive()
        .basic(clientId, clientSecret)
        .with()
        .param("grant_type", "password")
        .param("client_id", clientId)
        .param("username", username)
        .param("password", password)
        .when()
        .post("http://localhost:9999/uaa/oauth/token");

    return response.jsonPath()
            .getString("access_token");
  }

}
