package name.abhijitsarkar.javaee.spring.customscope;

public class ThreadAttributes {
    private String conversationId;

    public String getConversationId() {
	System.out.println("Returning conversation id: " + conversationId
		+ " for thread: " + Thread.currentThread().getName());
	
	return conversationId;
    }

    public void setConversationId(String conversationId) {
	this.conversationId = conversationId;
    }
}
