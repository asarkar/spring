package name.abhijitsarkar.javaee.spring.customscope;

public class RequestContextImpl implements RequestContext {
    private final UserManager userMgr;

    private String requestId;

    public RequestContextImpl() {
	this(null);
    }

    public RequestContextImpl(UserManager userMgr) {
	this.userMgr = userMgr;
    }

    @Override
    public String getUsername() {
	return userMgr.getUsername();
    }

    public String getRequestId() {
	return requestId;
    }

    public void setRequestId(String requestId) {
	this.requestId = requestId;
    }
}
