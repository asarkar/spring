package name.abhijitsarkar.javaee.spring.customscope;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContextManagerImpl implements ContextManager {
    @Autowired
    private UserManager userMgr;

    private Map<String, RequestContext> reqCtxMap = new HashMap<>();

    @Override
    public Object get(String name) {
	System.out.println("Retrieving bean against key: " + generateKey(name));

	return reqCtxMap.get(generateKey(name));
    }

    @Override
    public String getConversationId() {
	return ThreadContextHolder.currentThreadAttributes()
		.getConversationId();
    }

    @Override
    public Object remove(String name) {
	System.out.println("Removing bean against key: " + generateKey(name));

	return reqCtxMap.remove(generateKey(name));
    }

    @Override
    public void put(String name, RequestContext requestContext) {
	System.out.println("Putting bean against key: " + generateKey(name));

	reqCtxMap.put(generateKey(name), requestContext);

    }

    private String generateKey(String name) {
	return getConversationId() + "-" + name;
    }
}
