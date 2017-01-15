package name.abhijitsarkar.javaee.spring.customscope;

import static org.junit.Assert.assertEquals;

import javax.annotation.PostConstruct;

import name.abhijitsarkar.javaee.spring.customscope.async.MultithreadedRequestScopedBeansAsyncConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
	MultithreadedRequestScopedBeansProcessor.class,
	MultithreadedRequestScopedBeansAsyncConfig.class })
public class MultithreadedRequestScopeTest {
    @Autowired
    private Client client;

    @Autowired
    private ContextManager ctxMgr;

    @Autowired
    private UserManager userMgr;

    @PostConstruct
    public void initThreadContextHolder() {
	String conversationId = Thread.currentThread().getName();

	ThreadAttributes attr = new ThreadAttributes();
	attr.setConversationId(conversationId);
	ThreadContextHolder.setThreadAttributes(attr);

	RequestContextImpl reqCtx = new RequestContextImpl(userMgr);
	reqCtx.setRequestId(conversationId);

	ctxMgr.put("scopedTarget.requestContext", reqCtx);
    }

    @Test
    public void testUsername() {
	assertEquals("Abhijit", client.getUsername());
    }
}
