package name.abhijitsarkar.javaee.spring.customscope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThirdParty {
    @Autowired
    private RequestContext reqCtx;

    public ThirdParty() {
	System.out.println("Third party instantiated.");
    }

    public String getUsername() {
	System.out.println("ThirdParty.getUsername running in thread: " + Thread.currentThread().getName());
	
	return reqCtx.getUsername();
    }
}
