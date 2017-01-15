package name.abhijitsarkar.javaee.spring.customscope.async;

import java.util.concurrent.Executor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncAnnotationAdvisor;

public class MultithreadAwareAsyncAdvisor extends AsyncAnnotationAdvisor {
    private static final long serialVersionUID = -1297974218010159605L;

    public MultithreadAwareAsyncAdvisor(Executor executor,
	    AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler) {
	super(executor, asyncUncaughtExceptionHandler);
    }

    @Override
    protected Advice buildAdvice(Executor executor,
	    AsyncUncaughtExceptionHandler exceptionHandler) {
	return new MultithreadAwareAsyncExecutionInterceptor(executor,
		exceptionHandler);
    }
}
