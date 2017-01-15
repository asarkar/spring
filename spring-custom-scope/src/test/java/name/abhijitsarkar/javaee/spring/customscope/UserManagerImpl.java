package name.abhijitsarkar.javaee.spring.customscope;

import org.springframework.stereotype.Component;

@Component
public class UserManagerImpl implements UserManager {
    public UserManagerImpl() {
	System.out.println("UserManagerImpl instantiated.");
    }
    
    @Override
    public String getUsername() {
	return "Abhijit";
    }
}
