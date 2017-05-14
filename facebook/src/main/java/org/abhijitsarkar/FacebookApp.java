package org.abhijitsarkar;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.social.FacebookAutoConfiguration;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication(exclude = FacebookAutoConfiguration.class)
public class FacebookApp {
    public static void main(String[] args) {
        SpringApplication.run(FacebookApp.class, args);
    }

    @Configuration
    @RequiredArgsConstructor
    static class FacebookWebMvcConfig extends WebMvcConfigurerAdapter {
        private final ConnectionRepository connectionRepository;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            super.addInterceptors(registry);
            registry.addInterceptor(new FacebookConnectInterceptor(connectionRepository));
        }
    }

    @Configuration
    static class FacebookAutoConfiguration {
        @Configuration
        @EnableSocial
        @RequiredArgsConstructor
        protected static class FacebookConfigurerAdapter extends SocialAutoConfigurerAdapter {
            private final MoreFacebookProperties facebookProperties;

            @Bean
            @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
            public Facebook facebook(ConnectionRepository repository) {
                Connection<Facebook> connection = repository
                        .findPrimaryConnection(Facebook.class);
                return connection != null ? connection.getApi() : null;
            }

            @Bean(name = {"connect/facebookConnect", "connect/facebookConnected"})
            @ConditionalOnProperty(prefix = "spring.social", name = "auto-connection-views")
            public GenericConnectionStatusView facebookConnectView() {
                return new GenericConnectionStatusView("facebook", "Facebook");
            }

            @Override
            protected ConnectionFactory<?> createConnectionFactory() {
                return new FacebookConnectionFactory(facebookProperties);
            }
        }
    }
}
