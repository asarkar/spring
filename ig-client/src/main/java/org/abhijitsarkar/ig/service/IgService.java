package org.abhijitsarkar.ig.service;

import org.abhijitsarkar.ig.domain.AccessToken;
import org.abhijitsarkar.ig.domain.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

/**
 * @author Abhijit Sarkar
 */
public class IgService {
    private final ExchangeFunction igClient = WebClient.create(new ReactorClientHttpConnector());

    @Value("${CLIENT_ID}")
    private String clientId;
    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    @Autowired
    private IgProperties igProperties;

    public final String authorizationUrl(String callbackUrl) {
        String authorizationUrl = UriComponentsBuilder.fromUriString(igProperties.getAuthorizeUrl())
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", callbackUrl)
                .queryParam("response_type", "code")
                .build()
                .toUriString();
        return authorizationUrl;
    }

    public final Mono<Media> callback(String code, String callbackUrl) {
        return Mono.just(accessTokenParams(code, callbackUrl))
                .flatMap(params -> accessToken(igProperties.getAccessTokenUrl(), params))
                .flatMap(token -> top(recentPostsUrl(token)))
                .single();
    }

    private MultiValueMap<String, String> accessTokenParams(String code, String callbackUrl) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", callbackUrl);
        params.add("code", code);

        return params;
    }

    private String recentPostsUrl(AccessToken accessToken) {
        return String.format("%s?access_token=%s", igProperties.getRecentPostsUrl(), accessToken.getToken());
    }

    protected Mono<AccessToken> accessToken(String accessTokenUrl, MultiValueMap<String, String> queryParams) {
        ClientRequest<MultiValueMap<String, String>> request = ClientRequest.POST(accessTokenUrl)
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromObject(queryParams));

        return igClient.exchange(request)
                .then(response -> response.bodyToMono(AccessToken.class));
    }

    protected Mono<Media> top(String recentPostsUrl) {
        ClientRequest<Void> request = ClientRequest.GET(recentPostsUrl)
                .build();

        return igClient.exchange(request)
                .then(response -> response.bodyToMono(Media.class));
    }
}
