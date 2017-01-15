package name.abhijitsarkar.javaee.userpref.domain;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.util.StringUtils.trimWhitespace;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import name.abhijitsarkar.javaee.userpref.repository.JPAUserRepository;

@Component
@Scope(scopeName = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
@Slf4j
public class FakeSecurityContext implements SecurityContext {
    private static final String USERNAME_HDR = "x-loginId";
    private static final String PARTNER_ID_PARAM = "partnerId";

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JPAUserRepository userRepository;

    private String username;
    private String partnerId;

    @PostConstruct
    void init() {
	username = trimWhitespace(request.getHeader(USERNAME_HDR));
	partnerId = trimWhitespace(request.getParameter(PARTNER_ID_PARAM));

	Optional<UserEntity> user = userRepository.findOne(username);

	if (isNotAuthenticated(user)) {
	    throw new SecurityException(String.format(
		    "Username: %s and partner id: %s failed authentication.",
		    username, partnerId));
	}

	log.info("Authenticated username: {} and partner id: {}.", username,
		partnerId);
    }

    private boolean isNotAuthenticated(Optional<UserEntity> user) {
	return (!user.isPresent() || !user.get().getUsername().equals(username)
		|| !user.get().getPartnerId().equals(partnerId));
    }

    @Override
    public String getUsername() {
	return this.username;
    }

    @Override
    public String getPartnerId() {
	return this.partnerId;
    }
}
