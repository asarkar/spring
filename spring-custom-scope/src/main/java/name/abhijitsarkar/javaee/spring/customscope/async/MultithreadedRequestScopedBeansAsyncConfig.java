package name.abhijitsarkar.javaee.spring.customscope.async;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class MultithreadedRequestScopedBeansAsyncConfig {
    // @Bean
    // public Executor threadPoolTaskExecutor() {
    // ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // executor.setCorePoolSize(1);
    // executor.setMaxPoolSize(1);
    // executor.setQueueCapacity(100);
    // executor.setWaitForTasksToCompleteOnShutdown(true);
    //
    // return executor;
    // }
    //
    // @Override
    // public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    // return new MyAsyncExceptionHandler();
    // }
    //
    // private static class MyAsyncExceptionHandler implements
    // AsyncUncaughtExceptionHandler {
    // @Override
    // public void handleUncaughtException(Throwable throwable, Method method,
    // Object... obj) {
    // System.out.println("Exception message: " + throwable.getMessage());
    //
    // System.out.println("Method name: " + method.getName());
    // }
    // }
}
