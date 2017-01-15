package name.abhijitsarkar.javaee.spring.customscope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class Client {
    @Autowired
    private ThirdParty thirdParty;

    public Client() {
	System.out.println("Client instantiated.");
    }

    @Async
    public String getUsername() {
	System.out.println("Client.getUsername running in thread: " + Thread.currentThread().getName());
	
	return thirdParty.getUsername();
    }
}
