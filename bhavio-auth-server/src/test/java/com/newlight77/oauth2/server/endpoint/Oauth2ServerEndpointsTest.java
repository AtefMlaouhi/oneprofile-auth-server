package com.newlight77.oauth2.server.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Oauth2ServerEndpointsTest {

  @LocalServerPort
  protected int port;

  private TestRestTemplate testRestTemplate = new TestRestTemplate();

  @Test public void homePageProtected() {
    String url = String.format("http://localhost:%s/uaa/", port);

    ResponseEntity<String> response = testRestTemplate
        .getForEntity(url, String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    String auth = response.getHeaders().getFirst("WWW-Authenticate");
    assertTrue("Wrong header: " + auth, auth.startsWith("Bearer realm=\""));
  }

  @Test public void authorizationRedirects() {
    String url = String.format("http://localhost:%s/uaa", port);

    ResponseEntity<String> response = testRestTemplate
        .getForEntity(url +"/oauth/authorize", String.class);
    assertEquals(HttpStatus.FOUND, response.getStatusCode());
    String location = response.getHeaders().getFirst("Location");
    assertTrue("Wrong header: " + location,
        location.startsWith(url + "/login"));
  }

}
