package com.newlight77.oauth2.client;


import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Oauth2ResourceSsoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 9999)
public class OAuthAccessTokenIntegrationTest {

  private static String tokenValue = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MjcyODg5NzUsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiYWRhZmE3NTMtYThhNC00ODQ0LWI2ODYtOTQ4MmQwYTBkYWZiIiwiY2xpZW50X2lkIjoiYWNtZSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSIsImludGVybmFsX3NlcnZpY2VzIiwiZXh0ZXJuYWxfc2VydmljZXMiLCJhZG1pbiJdfQ.AbzuL2a64u9FvfNzBt97lq1e6GdTR0iuQ6ZaRBL1gQ9-Fv1ARq23UC0Q_vlAlqnd67x_ya7_aRjocyLNyJKtirgedapPMq5wOTNMRVEXv4aLL_kTJy_low-KEZZ26-hA57R6j37fh-OFNOFTfc1ndq83iOFwIhv8qWMS2gGGyLYpux6Pycu5DDMqoD4KoXGIc8FLNMolOa1ukJxJc4IVZjFZa8o0vo_LLONlkaFJYYCqrxqUvtWKvo7YJB0vYcM7gJ7-nMdrH3zfAVOjG8yoE6v5Rrr5nRpGyLFekpyAeQ4aqK5J0IYB-GXCK0eCbI56oM-dy0tY0VJXElnbHSfU5A";

  @Autowired
  private JwtTokenStore tokenStore;

  @LocalServerPort
  private int serverPort;

  @Value("${wiremock.server.port}")
  private int wiremockPort;

  @Test
  public void whenJwtPublicKeyIsValide_thenSuccess() {
    final OAuth2Authentication auth = tokenStore.readAuthentication(tokenValue);
    assertTrue(auth.isAuthenticated());
  }

  @Test
  public void whenAccessingResource_thenSuccess() throws Exception {
    stubingOauthTokenApi();
    stubingOauthAuthorizeApi();
    stubingSsoLogin();

    String tokenValue = requestAccessToken("acme", "acmesecret" ,"user", "password");
    OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);

    RestAssured.given()
        .header("Authorization", "Bearer " + accessToken.getValue())
        .when()
        .get(String.format("http://localhost:%s/sso/user", serverPort))
        .then()
        .statusCode(200)
        .body(Matchers.equalTo("user"))
    ;

  }

  private void stubingOauthTokenApi() throws Exception {
    JSONObject responseJson = new JSONObject()
            .put("access_token", tokenValue)
            .put("refress_token", tokenValue)
            .put("token_type", "bearer")
            .put("scope", "read write internal_services external_services admin")
            .put("expires_in", "3599")
            .put("jti", "adfb21a2-6ff7-481e-862c-7c5a1c958e54");

    ResponseDefinitionBuilder builder = aResponse()
            .withHeader("Content-Type", "application/json")
            .withHeader("Cache-Control", "no-store")
            .withHeader("Pragma", "no-cache")
            .withHeader("X-Content-Type-Options", "nosniff")
            .withHeader("X-XSS-Protection", "1; mode=block")
            .withHeader("X-Frame-Options", "DENY")
            .withHeader("Transfer-Encoding", "chunked")
            .withHeader("Date", Instant.now().toString())
            .withStatus(200)
            .withBody(responseJson.toString());

    stubFor(post(urlEqualTo("/uaa/oauth/token"))
            .withBasicAuth("acme", "acmesecret")
            .withRequestBody(containing("grant_type"))
            .withRequestBody(containing("client_id"))
            .withRequestBody(containing("username"))
            .withRequestBody(containing("password"))
            .willReturn(builder));
  }

  private void stubingOauthAuthorizeApi() throws Exception {
    ResponseDefinitionBuilder builder = aResponse()
            .withHeader("Content-Type", "text/html;charset=UTF-8")
            .withHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate")
            .withHeader("Expires", "0")
            .withHeader("Pragma", "no-cache")
            .withHeader("X-Content-Type-Options", "nosniff")
            .withHeader("X-XSS-Protection", "1; mode=block")
            .withHeader("X-Frame-Options", "DENY")
            .withHeader("Transfer-Encoding", "chunked")
            .withHeader("Location", String.format("http://localhost:%s/sso/login?response_type=code&state=68zvU4", serverPort))
            .withHeader("Date", Instant.now().toString())
            .withStatus(302)
            ;

    stubFor(get(urlMatching("/uaa/oauth/authorize.*"))
            .withQueryParam("client_id", equalTo("acme"))
            .withQueryParam("redirect_uri", equalTo(String.format("http://localhost:%s/sso/login", serverPort)))
            .withQueryParam("response_type", equalTo("code"))
            .willReturn(builder));
  }

  private void stubingSsoLogin() throws Exception {
    ResponseDefinitionBuilder builder = aResponse()
            .withHeader("Content-Type", "text/html;charset=UTF-8")
            .withHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate")
            .withHeader("Expires", "0")
            .withHeader("Pragma", "no-cache")
            .withHeader("X-Content-Type-Options", "nosniff")
            .withHeader("X-XSS-Protection", "1; mode=block")
            .withHeader("X-Frame-Options", "DENY")
            .withHeader("Transfer-Encoding", "chunked")
//            .withHeader("Set-Cookie", "JSESSIONID=304C1A88F58670BFA5FA7E5EA0B6EB0D; Path=/sso; HttpOnly")
//            .withHeader("Set-Cookie", "XSRF-TOKEN=; Max-Age=0; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/sso")
//            .withHeader("Set-Cookie", "XSRF-TOKEN=589228c1-b067-4428-b4b3-81484cd87dc3; Path=/sso")
            .withHeader("Cookie", "JSESSIONID=8AD3D2985241DC3C55061D33299B5CCC; XSRF-TOKEN=6cd265a1-66d6-4e3c-8c91-fe7cc3cc8a41; XSRF-TOKEN=aa61b439-087e-47ac-a7d6-61b5285851ad")
            .withHeader("Location", String.format("http://localhost:%s/sso/user", serverPort))
            .withHeader("Referer", "http://localhost:9999/uaa/login")
            .withHeader("Date", Instant.now().toString())
            .withStatus(200) // supposed to be 302 and redirection to /sso/user, but circuit-break, as it would be circular redirection
            .withBody("user")
            ;

    stubFor(get(urlMatching(".*/login.*"))
            .withQueryParam("response_type", equalTo("code"))
            .withQueryParam("state", WireMock.matching(".*"))
            .willReturn(builder));
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
        .post(String.format("http://localhost:%s/uaa/oauth/token", wiremockPort));

    return response.jsonPath()
            .getString("access_token");
  }

}
