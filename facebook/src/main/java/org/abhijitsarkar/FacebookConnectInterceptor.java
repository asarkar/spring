package org.abhijitsarkar;

import lombok.RequiredArgsConstructor;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
public class FacebookConnectInterceptor extends HandlerInterceptorAdapter {
    private static final String FACEBOOK_CONNECT_URI = "/connect/facebook";

    private final ConnectionRepository connectionRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (ifNotConnected() && ifNotConnectURI(request.getRequestURI())) {
            response.sendRedirect(String.format("%s%s", request.getContextPath(), FACEBOOK_CONNECT_URI));

            return false;
        }

        return super.preHandle(request, response, handler);
    }

    private boolean ifNotConnected() {
        return connectionRepository.findPrimaryConnection(Facebook.class) == null;
    }

    private boolean ifNotConnectURI(String uri) {
        return !FACEBOOK_CONNECT_URI.equals(uri);
    }
}
