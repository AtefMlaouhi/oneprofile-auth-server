package com.newlight77.oauth2.server.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginEnpointTest {

  @LocalServerPort
  private int port;

  private TestRestTemplate template = new TestRestTemplate();

  private ResponseEntity<Void> token() {
    String url = String.format("http://localhost:%s", port);
    String tokenUrl = String.format(url + "/uaa/login", port);

    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
    form.set("grant_type", "password");
    form.set("client_id", "acme");
    form.set("client_secret", "acmesecret");
    form.set("username", "user");
    form.set("password", "password");

    HttpHeaders headers = new HttpHeaders();
    RequestEntity<MultiValueMap<String, String>> request =
        new RequestEntity<>(form, headers, HttpMethod.POST, URI.create(tokenUrl));
    ResponseEntity<Void> location = template.exchange(request, Void.class);
    assertEquals(url, location.getHeaders().getFirst("Location"));

    return location;
  }

//  @Test public void loginSucceeds() {
//    String url = String.format("http://localhost:%s/uaa/login", port);
//
//    ResponseEntity<Void> response = token();
//    String loginUrl = response.getHeaders().getFirst("Location");
//
////    String csrf = getCsrf(response.getBody());
//    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
//    form.set("username", "user");
//    form.set("password", "password");
////    form.set("_csrf", csrf);
//    HttpHeaders headers = new HttpHeaders();
//        headers.put("COOKIE", response.getHeaders().get("Set-Cookie"));
//    RequestEntity<MultiValueMap<String, String>> request =
//        new RequestEntity<>(form, headers, HttpMethod.POST, URI.create(loginUrl));
//    ResponseEntity<Void> location = template.exchange(request, Void.class);
//    assertEquals(url, location.getHeaders().getFirst("Location"));
//  }

  @Test
  public void loginSucceeds() {
    String url = String.format("http://localhost:%s", port);
    String loginUrl = String.format(url + "/uaa/login", port);

    ResponseEntity<String> response = template.getForEntity(loginUrl, String.class);
    String csrf = getCsrf(response.getBody());
    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
    form.set("username", "user");
    form.set("password", "password");
    form.set("_csrf", csrf);
    HttpHeaders headers = new HttpHeaders();
    headers.put("COOKIE", response.getHeaders().get("Set-Cookie"));
    RequestEntity<MultiValueMap<String, String>> request = new RequestEntity<MultiValueMap<String, String>>(
        form, headers, HttpMethod.POST, URI.create(loginUrl));
    ResponseEntity<Void> location = template.exchange(request, Void.class);
    assertEquals(url + "/uaa/", location.getHeaders().getFirst("Location"));
  }

  private String getCsrf(String soup) {
    Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*").matcher(soup);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return null;
  }
}
