package com.wootech.transtalk.client;

import com.wootech.transtalk.dto.auth.GoogleApiRequest;
import com.wootech.transtalk.dto.auth.GoogleApiResponse;
import com.wootech.transtalk.dto.auth.GoogleProfileResponse;
import com.wootech.transtalk.exception.custom.GoogleApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleClient {

    private final RestClient restClient = RestClient.create();

    @Value("${spring.oauth.google.client.id}")
    private String clientId;
    @Value("${spring.oauth.google.client.secret}")
    private String clientSecret;
    @Value("${spring.oauth.google.client.uri.profile}")
    private String profileUri;
    @Value("${spring.oauth.google.client.uri.token}")
    private String tokenUri;
    @Value("${spring.oauth.google.client.redirection}")
    private String redirectUri;

    public static final String BEARER_PREFIX = "Bearer ";

    public URI buildAuthorizeApiUri() {
        return UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .queryParam("access_type", "offline")
                .build()
                .toUri();
    }

    public String requestToken(String code) {
         URI uri = UriComponentsBuilder.fromUriString(this.tokenUri)
                 .build()
                 .toUri();
         log.info("[GoogleClient] API URI: {}", uri);

        GoogleApiRequest tokenRequest = GoogleApiRequest.builder()
                .code(code)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .grantType("authorization_code")
                .build();

         GoogleApiResponse tokenResponse = restClient.post()
                 .uri(uri)
                 .header(HttpHeaders.ACCEPT)
                 .acceptCharset(StandardCharsets.UTF_8)
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(tokenRequest)
                 .retrieve()
                 .onStatus(HttpStatusCode::isError, ((request, response) -> {
                     String body = new String(response.getBody().readAllBytes());
                     log.error("[GoogleAPI] Raw Response: {}", body);

                     throw new GoogleApiException("Token Request Failed: " + body);
                 }))
                 .body(GoogleApiResponse.class);

         log.info("[GoogleAPI] Token Response: {}", tokenResponse);

         String accessToken = extractAccessCode(tokenResponse);
         return accessToken;
    }

    private String extractAccessCode(GoogleApiResponse response) {
        if (response == null || response.getAccessToken() == null || response.getAccessToken().isEmpty()) {
            log.warn("[GoogleAPI] Empty Response From Google");
            return "";
        }
        return response.getAccessToken();
    }

    public GoogleProfileResponse requestProfile(String accessToken) {
        URI uri = UriComponentsBuilder.fromUriString(profileUri)
                .build()
                .toUri();
        log.info("[GoogleClient] API URI: {}", uri);

        GoogleProfileResponse googleProfileResponse = restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX  + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    String body = new String(response.getBody().readAllBytes());
                    log.error("[GoogleAPI] Raw Response: {}", body);

                    throw new GoogleApiException("Profile Request Failed: " + body);
                }))
                .body(GoogleProfileResponse.class);

        log.info("[GoogleAPI] Profile Response: {}", googleProfileResponse);
        return googleProfileResponse;
    }


}
