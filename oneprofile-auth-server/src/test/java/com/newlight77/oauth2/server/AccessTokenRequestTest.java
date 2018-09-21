package com.newlight77.oauth2.server;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthServerApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccessTokenRequestTest {

  @Autowired
  @LocalServerPort
  private int serverPort;

  @Autowired
  private JwtTokenStore tokenStore;

  @Test
  public void whenRequestingAccessTokenUsingPasswordGrantType_thenSuccess() {
    final AccessToken accessToken =
        AccessTokenUtil.requestAccessToken(serverPort, "sso", "ssosecret", "benoit", "password");
    assertNotNull(accessToken.getAccessToken());
    assertNotNull(accessToken.getRefreshToken());

    final OAuth2Authentication auth = tokenStore.readAuthentication(accessToken.getAccessToken());
    assertTrue(auth.isAuthenticated());

  }

  @Test // if keystore changed, public key on client side must be updated
  public void whenJwtPublicKeyIsValid_thenSuccess() {
    String tokenValue = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MjcyODg5NzUsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiYWRhZmE3NTMtYThhNC00ODQ0LWI2ODYtOTQ4MmQwYTBkYWZiIiwiY2xpZW50X2lkIjoiYWNtZSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSIsImludGVybmFsX3NlcnZpY2VzIiwiZXh0ZXJuYWxfc2VydmljZXMiLCJhZG1pbiJdfQ.AbzuL2a64u9FvfNzBt97lq1e6GdTR0iuQ6ZaRBL1gQ9-Fv1ARq23UC0Q_vlAlqnd67x_ya7_aRjocyLNyJKtirgedapPMq5wOTNMRVEXv4aLL_kTJy_low-KEZZ26-hA57R6j37fh-OFNOFTfc1ndq83iOFwIhv8qWMS2gGGyLYpux6Pycu5DDMqoD4KoXGIc8FLNMolOa1ukJxJc4IVZjFZa8o0vo_LLONlkaFJYYCqrxqUvtWKvo7YJB0vYcM7gJ7-nMdrH3zfAVOjG8yoE6v5Rrr5nRpGyLFekpyAeQ4aqK5J0IYB-GXCK0eCbI56oM-dy0tY0VJXElnbHSfU5A";

    final AccessToken accessToken = AccessTokenUtil.fakeAccessToken(tokenValue, tokenValue);
    final OAuth2Authentication auth = tokenStore.readAuthentication(accessToken.getAccessToken());
    assertTrue(auth.isAuthenticated());
  }

}
