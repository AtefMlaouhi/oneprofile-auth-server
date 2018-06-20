# Spring-boot 2 with OAuth2 - client and server demo


## Introduction

- Spring Boot 2 (2.1.0.BUILD_SNAPSHOT)
- Spring Security (5.0.6.RELEASE)
- Spring Security OAuth2 (2.2.1.RELEASE)
- Spring Security JWT (1.0.9.RELEASE)


## Pre-requisites

- docker + docker-compose
- maven
- JDK 8+


## Running the example

```bash
mvn clean package
docker-compose up
```

1. Open a browser to [http://localhost:8080/sso/user](). 
   This user resource is protected. 

2. Trying to access a protected resource will be automatically redirect to the [`/oauth/authorize`](http://localhost:8888/uaa/oauth/authorize) endpoint to start an _authorization code_ flow. 
   In the process it authenticates itself as 'acme' client. 

3. For the first time, the authorization server redirects to its login page. 
   You can use one of the following username/password combinations to login:
    - `user:password` (has USER role)
    - `sysuser:password` (has SYSTEM role)
4. After a successful login you are redirected to the [`/oauth/confirm_access`](http://localhost:8888/uaa/oauth/confirm_access) endpoint where you need to approve all grants requested by the client.

5. The authorization server now redirects back to the client on a pre-approved URI, with an _authorization code_. 

6. The client then accesses the authorization server on the [`/oauth/token`](http://localhost:8888/oauth/token) endpoint to retrieve an access token (including a refresh token) with the authorization code. 
   _This token is actually a JSON Web Token (JWT)._

7. The client now retries to request the user resource on resource server, which is the client itself. 
   The resource server accepts the JWT token and checks the signature using the authorization server's public key. 
   There's no communication necessary between the resource server and the authorization server; that is one of the nice things about JWT. The JWT token also describes the user's roles, which are checked against the authorization requirements of the resource.

8. As the request is successfully authenticated, the client receives the resource (a JSON representation of the user principal) and show it to the browser.


### Generate your own JKS key

This will require :
- open-ssl
- JDK keytool

```bash
keytool -genkeypair -alias oauth \
                    -keyalg RSA \
                    -keypass testpass \
                    -keystore keystore.jks \
                    -storepass testpass
```

To export our Public key from generated JKS with :
- password = testpass 

```bash
keytool -list -rfc --keystore keystore.jks | openssl x509 -inform pem -pubkey -noout
```

### Access token


Request an access token using the client credentials

```bash
curl acme:acmesecret@localhost:8888/uaa/oauth/token -d grant_type=client_credentials
```

response :
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIiwid3JpdGUiLCJpbnRlcm5hbF9zZXJ2aWNlcyIsImV4dGVybmFsX3NlcnZpY2VzIiwiYWRtaW4iXSwiZXhwIjoxNTI3OTc1NDk3LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiOGY1MDZkYjctMWMwNy00YWVjLWExMDUtZTg2Y2QxZGY4Mzc1IiwiY2xpZW50X2lkIjoiYWNtZSJ9.SpRFv8XZ1sKXQT_jDNfkIkrh6SsNyGVC7kEg-U5piYxYueOe8HJGUtGvL_za4R4J3XC5WPjFPTr2SMiniJs1hpCFnPBFRmqx-WXtpVob8Wbd1Wc7VZJmu3c6OvM4ZOAVAUZzvzH8zPmV_gHc9LYnk0O0GJ19cTvZbCzs5xm1E9Txec2GQ8IjFgqwpAfKBl2Yx8lBnIuC42Crwt8U4lCNRRvnOPGQj18azcm4vCxzCn8PFPpXL-25gb0gdUvFjVz9pXcselUP16JvlHGSsQYXRGIjY1cQIXQRtmq9-BmmMPfydZ78-m4SzSRKwAIes8EivHLAn1HMKfA421y896wwpQ",
  "token_type": "bearer",
  "expires_in": 3599,
  "scope": "read write internal_services external_services admin",
  "jti": "8f506db7-1c07-4aec-a105-e86cd1df8375"
}
```

Request an access token using the user/password 
```bash
curl acme:acmesecret@localhost:8888/uaa/oauth/token -d grant_type=password -d username=user -d password=password -d scope=read

```

response :
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1Mjc5NzYyNzIsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiOTM5MDk0NTktNmY3NS00YWU3LWI2M2EtOWExMTNhMGZkNTFhIiwiY2xpZW50X2lkIjoiYWNtZSIsInNjb3BlIjpbInJlYWQiXX0.P8srLwfYthaaUg3mDmS7gYa_RM-aeYdWMlF03PFWjupZvf5SAsKOP6ppD8T62IdEKXTr0HyQ3csnXRqR9I2hGsy3Mh1qx9XDuzK6oE1rXPVqlJVFhlwV3psCI0Jq588hnKhg6ARvpigrJhnddORcXf7NUIJorPYy5sxg-TZdPNKXQe9BhUVHwUTvlLgCHTfVQUEI686LEi0WsNZFcF2uPFyDqDqLIIyLPsoZo-rjJpp7UPCMg4g7AuzRtNfsS55NIROmkDqn_oCmDk_vgSPX6K2HUx-Bom1ptQEBYQ72IhSFn4bihZGzVwwOIFKEcw4BJN0yq3iQGa9hl4w1SW7hdw",
  "token_type": "bearer",
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1c2VyIiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiI5MzkwOTQ1OS02Zjc1LTRhZTctYjYzYS05YTExM2EwZmQ1MWEiLCJleHAiOjE1MzA1NjQ2NzIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiIzOTVhMzVjMy0yZDBjLTQzMmYtOGVlNi01ZTBhNjc1YjAwMDEiLCJjbGllbnRfaWQiOiJhY21lIn0.BDtSDvOXohSiYYpoduCCSoFPIbL-SCe__C80CgQyWRVZIqo9o9thodVw0TezXZhiVwqoH0odyyLT8xNJm8dMJGfvnSALMjP-Qy3sOJhyUWGo31siBcw-JUrwqsEE4mfWIOATUOCK897YDZeG9Ppu0mvKsKIQ7iHdpLoKh9m49fBHi5huIfCqGUvN-8riofIs-pbD6DNk7z963jonwxr0cOjbhF2F4QSuQKC-lz_DJIRfn3h8eoMNVxQFzhid26UpbkWWUNT4Q-xtaMCKAq2Q_e4pdaEGk5ML_SEdBa2dyEVxbLc351mW-j9Bbt05GibzKWgluBr-TccIdwvxssLb2g",
  "expires_in": 3599,
  "scope": "read",
  "jti": "93909459-6f75-4ae7-b63a-9a113a0fd51a"
}
```

**References:**

- [spring-security-oauth](https://github.com/spring-projects/spring-security-oauth)
- [OAuth 2 Developers Guide](http://projects.spring.io/spring-security-oauth/docs/oauth2.html)

