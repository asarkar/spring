package org.abhijitsarkar;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookAdapter;

/**
 * @author Abhijit Sarkar
 */
public class FacebookConnectionFactory extends OAuth2ConnectionFactory<Facebook> {
    public FacebookConnectionFactory(MoreFacebookProperties facebookProperties) {
        super("facebook", new FacebookServiceProvider(facebookProperties), new FacebookAdapter());
    }
}
