package org.abhijitsarkar;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Abhijit Sarkar
 */
public class FacebookServiceProvider extends AbstractOAuth2ServiceProvider<Facebook> {
    private static final String AUTHORIZE_URL = "https://www.facebook.com/v{apiVersion}/dialog/oauth";
    private static final String ACCESS_TOKEN_URL = "{graphApiUrl}/oauth/access_token";
    private static final String GRAPH_API_URL = "https://graph.facebook.com/v{apiVersion}";

    private final String appNamespace;
    private final String apiVersion;

    public FacebookServiceProvider(MoreFacebookProperties facebookProperties) {
        super(getOAuth2Template(facebookProperties.getAppId(), facebookProperties.getAppSecret(),
                facebookProperties.getApiVersion()));
        this.appNamespace = facebookProperties.getAppNamespace();
        this.apiVersion = facebookProperties.getApiVersion();
    }

    private static OAuth2Template getOAuth2Template(String appId, String appSecret, String apiVersion) {
        OAuth2Template oAuth2Template = new OAuth2Template(appId, appSecret,
                authorizeUrl(apiVersion),
                accessTokenUrl(apiVersion));
        oAuth2Template.setUseParametersForClientAuthentication(true);
        return oAuth2Template;
    }

    private static String authorizeUrl(String apiVersion) {
        return UriComponentsBuilder.fromUriString(AUTHORIZE_URL)
                .buildAndExpand(apiVersion)
                .toUriString();
    }

    private static String accessTokenUrl(String apiVersion) {
        return UriComponentsBuilder.fromUriString(ACCESS_TOKEN_URL)
                .buildAndExpand(graphApiUrl(apiVersion))
                .toUriString();
    }

    private static String graphApiUrl(String apiVersion) {
        return UriComponentsBuilder.fromUriString(GRAPH_API_URL)
                .buildAndExpand(apiVersion)
                .toUriString();
    }

    public Facebook getApi(String accessToken) {
        FacebookTemplate facebookTemplate = new FacebookTemplate(accessToken, appNamespace);
        facebookTemplate.setApiVersion(apiVersion);

        return facebookTemplate;
    }
}
