package com.newlight77.oauth2.server;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccessTokenRevokeTest {

  @Autowired @LocalServerPort private int serverPort;

  @Autowired private JwtTokenStore tokenStore;

  @Test
  @Ignore
  public void whenRevokeToken_thenTokenInvalidError() {
    final AccessToken
        accessToken1 =
        AccessTokenUtil.requestAccessToken(serverPort, "sso", "ssosecret", "benoit", "password");
    final AccessToken
        accessToken2 =
        AccessTokenUtil.requestAccessToken(serverPort, "sso", "ssosecret", "fabien", "password");

    final AccessToken
        accessToken3 =
        AccessTokenUtil.refreshToken(serverPort, "sso", "ssosecret", accessToken1.getRefreshToken());

    AccessTokenUtil.authorizeClient(serverPort, "sso", "ssosecret");

    // accessToken1 has been refreshed : given accessToken3
    final Response
        tokenResponse1 =
        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken1.getAccessToken())
            .get(String.format("http://localhost:%s/uaa/user", serverPort));
    assertEquals(404, tokenResponse1.getStatusCode());

    // success : accessToken2 is valid
    final Response
        tokenResponse2 =
        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken2.getAccessToken())
            .get(String.format("http://localhost:%s/uaa/user", serverPort));
    assertEquals(200, tokenResponse2.getStatusCode());

    // revoke accessToken2
    final Response
        revokeResponse3 =
        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken2.getAccessToken())
            .delete(String.format("http://localhost:%s/uaa/oauth/token/", serverPort) + accessToken2.getAccessToken());
    assertEquals(200, revokeResponse3.getStatusCode());

    // success : accessToken2 is no longer valid
    final Response
        tokenResponse4 =
        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken2.getAccessToken())
            .get(String.format("http://localhost:%s/uaa/user", serverPort));
    assertEquals(401, tokenResponse4.getStatusCode());

    // success : accessToken3 is valid
    final Response
        tokenResponse5 =
        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken3.getAccessToken())
            .get(String.format("http://localhost:%s/uaa/user", serverPort));
    assertEquals(200, tokenResponse5.getStatusCode());

  }

}
