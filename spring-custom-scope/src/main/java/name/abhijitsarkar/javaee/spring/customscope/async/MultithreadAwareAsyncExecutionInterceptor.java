package name.abhijitsarkar.javaee.spring.customscope.async;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import name.abhijitsarkar.javaee.spring.customscope.ThreadContextHolder;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AnnotationAsyncExecutionInterceptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.concurrent.ListenableFuture;

public class MultithreadAwareAsyncExecutionInterceptor extends
	AnnotationAsyncExecutionInterceptor {
    // Java 8's CompletableFuture type present?
    private static final boolean completableFuturePresent = ClassUtils
	    .isPresent("java.util.concurrent.CompletableFuture",
		    AnnotationAsyncExecutionInterceptor.class.getClassLoader());

    public MultithreadAwareAsyncExecutionInterceptor(Executor defaultExecutor,
	    AsyncUncaughtExceptionHandler exceptionHandler) {
	super(defaultExecutor, exceptionHandler);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
	Class<?> targetClass = (invocation.getThis() != null ? AopUtils
		.getTargetClass(invocation.getThis()) : null);
	Method specificMethod = ClassUtils.getMostSpecificMethod(
		invocation.getMethod(), targetClass);
	final Method userDeclaredMethod = BridgeMethodResolver
		.findBridgedMethod(specificMethod);

	AsyncTaskExecutor executor = determineAsyncExecutor(userDeclaredMethod);
	if (executor == null) {
	    throw new IllegalStateException(
		    "No executor specified and no default executor set on AsyncExecutionInterceptor either");
	}

	Callable<Object> task = new MultithreadAwareCallable(invocation,
		ThreadContextHolder.currentThreadAttributes()
			.getConversationId(), userDeclaredMethod);

	Class<?> returnType = invocation.getMethod().getReturnType();
	if (completableFuturePresent) {
	    Future<Object> result = CompletableFutureDelegate
		    .processCompletableFuture(returnType, task, executor);
	    if (result != null) {
		return result;
	    }
	}
	if (ListenableFuture.class.isAssignableFrom(returnType)) {
	    return ((AsyncListenableTaskExecutor) executor)
		    .submitListenable(task);
	} else if (Future.class.isAssignableFrom(returnType)) {
	    return executor.submit(task);
	} else {
	    executor.submit(task);
	    return null;
	}
    }

    private class MultithreadAwareCallable implements Callable<Object> {
	private final MethodInvocation invocation;
	private final Method userDeclaredMethod;
	private final String conversationId;

	private MultithreadAwareCallable(MethodInvocation invocation,
		String conversationId, Method userDeclaredMethod) {
	    this.invocation = invocation;
	    this.userDeclaredMethod = userDeclaredMethod;
	    this.conversationId = conversationId;
	}

	@Override
	public Object call() throws Exception {
	    try {
		ThreadContextHolder.currentThreadAttributes()
			.setConversationId(conversationId);

		Object result = invocation.proceed();
		if (result instanceof Future) {
		    return ((Future<?>) result).get();
		}
	    } catch (ExecutionException ex) {
		handleError(ex.getCause(), userDeclaredMethod,
			invocation.getArguments());
	    } catch (Throwable ex) {
		handleError(ex, userDeclaredMethod, invocation.getArguments());
	    }
	    return null;
	}
    }

    private static class CompletableFutureDelegate {
	public static <T> Future<T> processCompletableFuture(
		Class<?> returnType, final Callable<T> task, Executor executor) {
	    if (!CompletableFuture.class.isAssignableFrom(returnType)) {
		return null;
	    }
	    return CompletableFuture.supplyAsync(new Supplier<T>() {
		@Override
		public T get() {
		    try {
			return task.call();
		    } catch (Throwable ex) {
			throw new CompletionException(ex);
		    }
		}
	    }, executor);
	}
    }

}
