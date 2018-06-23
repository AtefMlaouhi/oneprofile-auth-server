package com.newlight77.oauth2.server;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class AccessTokenUtil {

  public static AccessToken fakeAccessToken(String accessToken, String refreshToken) {
    return AccessToken.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken).build();
  }

  public static AccessToken requestAccessToken(int serverPort, String clientId, String clientSecret, String username, String password) {
    final Map<String, String> params = new HashMap();
    params.put("grant_type", "password");
    params.put("client_id", clientId);
    params.put("username", username);
    params.put("password", password);
    Response response =  RestAssured.given()
        .auth()
        .preemptive()
        .basic(clientId, clientSecret)
        .and()
        .with()
        .params(params)
        .when()
        .post(String.format("http://localhost:%s/uaa/oauth/token", serverPort));

    return AccessToken.builder()
        .accessToken(response.jsonPath().getString("access_token"))
        .refreshToken(response.jsonPath().getString("refresh_token")).build();
  }

  public static AccessToken refreshToken(int serverPort, String clientId, String clientSecret, String refreshToken) {
    final Map<String, String> params = new HashMap();
    params.put("grant_type", "refresh_token");
    params.put("client_id", clientId);
    params.put("refresh_token", refreshToken);
    Response response =  RestAssured.given()
        .auth()
        .preemptive()
        .basic(clientId, clientSecret)
        .and()
        .with()
        .params(params)
        .when()
        .post(String.format("http://localhost:%s/uaa/oauth/token", serverPort));

    return AccessToken.builder()
        .accessToken(response.jsonPath().getString("access_token"))
        .refreshToken(response.jsonPath().getString("refresh_token")).build();
  }

  public static void authorizeClient(int serverPort, String clientId, String clientSecret) {
    final Map<String, String> params = new HashMap();
    params.put("response_type", "code");
    params.put("client_id", clientId);
    params.put("scope", "read,write");
    RestAssured.given()
        .auth()
        .preemptive()
        .basic(clientId, "secret")
        .and()
        .with()
        .params(params)
        .when()
        .post(String.format("http://localhost:%s/uaa/oauth/authorize", serverPort));
  }
}
