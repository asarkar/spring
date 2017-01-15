package org.abhijitsarkar.ig.web;

import org.abhijitsarkar.ig.domain.Media;
import org.abhijitsarkar.ig.service.IgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.util.ReflectionUtils.findMethod;


/**
 * @author Abhijit Sarkar
 */
@RestController
public class IgController {
    @Autowired
    private IgService igService;

    private String callbackPath;

    @PostConstruct
    void postConstruct() {
        Method callback = findMethod(getClass(), "callback", String.class, ServerHttpRequest.class);
        GetMapping annotation = findAnnotation(callback, GetMapping.class);

        callbackPath = annotation.value()[0];
    }

    @GetMapping("/")
    public ResponseEntity<Void> redirect(ServerHttpRequest request) {
        String callbackUrl = callbackUrl(request);
        return ResponseEntity.status(TEMPORARY_REDIRECT)
                .location(URI.create(igService.authorizationUrl(callbackUrl)))
                .build();
    }

    @GetMapping(value = "/callback", produces = APPLICATION_JSON_UTF8_VALUE)
    public Mono<Media> callback(@RequestParam("code") String code, ServerHttpRequest request) {
        String callbackUrl = callbackUrl(request);
        return igService.callback(code, callbackUrl);
    }

    private String callbackUrl(ServerHttpRequest request) {
        try {
            URI uri = request.getURI();
            String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
            String baseUrl = String.format("%s://%s%s", scheme, uri.getAuthority(), callbackPath);
            return baseUrl.replaceFirst(
                    "localhost|127\\.0\\.0\\.1|\\[0:0:0:0:0:0:0:1\\]", InetAddress.getLocalHost().getHostAddress()
            );
        } catch (UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }
}
