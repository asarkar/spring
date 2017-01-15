package name.abhijitsarkar.javaee.spring.customscope;

public interface ContextManager {
    Object get(String name);

    Object remove(String name);

    void put(String name, RequestContext requestContext);
    
    String getConversationId();
}
