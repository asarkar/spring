package org.abhijitsarkar.ig.service;

import org.abhijitsarkar.ig.domain.AccessToken;
import org.abhijitsarkar.ig.domain.Media;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

/**
 * @author Abhijit Sarkar
 */
public class RestOperationsIgService extends IgService {
    private final RestOperations restTemplate = new RestTemplate();

    @Override
    protected final Mono<AccessToken> accessToken(String accessTokenUrl, MultiValueMap<String, String> queryParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(queryParams, headers);

        return Mono.fromCallable(() -> restTemplate.postForEntity(accessTokenUrl, request, AccessToken.class))
                .map(ResponseEntity::getBody);
    }

    @Override
    protected final Mono<Media> top(String recentPostsUrl) {
        return Mono.fromCallable(() -> restTemplate.getForObject(recentPostsUrl, Media.class));
    }
}
