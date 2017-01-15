package name.abhijitsarkar.javaee.spring.customscope.async;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class MultithreadAwareAsyncBeanPostProcessor extends
	AsyncAnnotationBeanPostProcessor {
    private static final long serialVersionUID = 3924635230115091840L;

    // @Override
    // public void setExecutor(Executor executor) {
    // super.setExecutor(defaultExecutor());
    // }

    @Bean
    static Executor threadPoolTaskExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	executor.setCorePoolSize(1);
	executor.setMaxPoolSize(1);
	executor.setQueueCapacity(100);
	executor.setWaitForTasksToCompleteOnShutdown(true);

	return executor;
    }

    @Bean
    static AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
	return new MyAsyncExceptionHandler();
    }

    private static class MyAsyncExceptionHandler implements
	    AsyncUncaughtExceptionHandler {
	@Override
	public void handleUncaughtException(Throwable throwable, Method method,
		Object... obj) {
	    System.out.println("Exception message: " + throwable.getMessage());

	    System.out.println("Method name: " + method.getName());
	}
    }

    @Bean
    static Executor defaultExecutor() {
	return new SimpleAsyncTaskExecutor();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
	// try {
	super.setBeanFactory(beanFactory);

	/*
	 * The class in our case is a proxy, meaning a subclass of this class.
	 */
	// Class<?> superclass = getClass().getSuperclass().getSuperclass();
	//
	// /* I wish there were getters on the private fields but only setters
	// */
	// Field executor = superclass.getDeclaredField("executor");
	// executor.setAccessible(true);
	//
	// Field exceptionHandler = superclass
	// .getDeclaredField("exceptionHandler");
	// exceptionHandler.setAccessible(true);
	//
	// Field asyncAnnotationType = superclass
	// .getDeclaredField("asyncAnnotationType");
	// asyncAnnotationType.setAccessible(true);

	this.advisor = new MultithreadAwareAsyncAdvisor(defaultExecutor(),
		getAsyncUncaughtExceptionHandler());

	// if (asyncAnnotationType.get(this) != null) {
	// advisor.setAsyncAnnotationType((Class<? extends Annotation>)
	// asyncAnnotationType
	// .get(this));
	// }

	((MultithreadAwareAsyncAdvisor) this.advisor)
		.setBeanFactory(beanFactory);

	// } catch (ReflectiveOperationException e) {
	// throw new BeanInitializationException(
	// "Failed to hack private fields", e);
	// }
    }
}
