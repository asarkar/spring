package org.abhijitsarkar.ig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.abhijitsarkar.ig.domain.AccessToken;
import org.abhijitsarkar.ig.domain.Media;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singleton;

/**
 * @author Abhijit Sarkar
 */
@Configuration
public class IgTestConfiguration {
    @RestController
    public static class MockIg {
        private final ObjectReader reader = new ObjectMapper().readerFor(Media.class);

        @PostMapping("/oauth/access_token")
        public AccessToken accessToken() {
            AccessToken token = new AccessToken();
            token.setToken("whatever");

            return token;
        }

        @GetMapping("/v1/users/self/media/recent")
        public Media recentPosts() throws IOException {
            Media.Likes likes = new Media.Likes();
            likes.setCount(1);
            Media.Medium medium = new Media.Medium();
            medium.setLink("http://whatever");
            medium.setLikes(likes);

            Media media = new Media();
            media.setMedia(singleton(medium));

            return media;
        }
    }
}
