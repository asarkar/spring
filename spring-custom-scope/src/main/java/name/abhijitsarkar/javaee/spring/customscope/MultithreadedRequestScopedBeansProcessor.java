package name.abhijitsarkar.javaee.spring.customscope;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
@ComponentScan
public class MultithreadedRequestScopedBeansProcessor implements
	BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(
	    ConfigurableListableBeanFactory beanFactory) throws BeansException {
	org.springframework.beans.factory.config.Scope multithreadedRequestScope = new MultithreadedRequestScope(
		beanFactory);

	beanFactory.registerScope(multithreadedRequestScope.getClass()
		.getSimpleName(), multithreadedRequestScope);

	BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

	for (String beanName : beanFactory.getBeanDefinitionNames()) {
	    BeanDefinition definition = beanFactory.getBeanDefinition(beanName);

	    System.out.println("Post processing bean: " + beanName
		    + ", scope: " + definition.getScope());

	    if (MultithreadedRequestScope.NAME.equals(definition.getScope())) {
		BeanDefinitionHolder proxyHolder = ScopedProxyUtils
			.createScopedProxy(new BeanDefinitionHolder(definition,
				beanName), registry, false);
		registry.registerBeanDefinition(beanName,
			proxyHolder.getBeanDefinition());
	    }
	}
    }

    @Override
    public void postProcessBeanDefinitionRegistry(
	    BeanDefinitionRegistry registry) throws BeansException {
	// BeanDefinitionBuilder reqCtxImpl1Bldr = BeanDefinitionBuilder
	// .genericBeanDefinition(RequestContextImpl.class).setScope(
	// MultithreadedRequestScope.NAME);
	// registry.registerBeanDefinition("requestContextImpl1",
	// reqCtxImpl1Bldr.getBeanDefinition());
	//
	// BeanDefinitionBuilder reqCtxImpl2Bldr = BeanDefinitionBuilder
	// .genericBeanDefinition(RequestContextImpl2.class).setScope(
	// MultithreadedRequestScope.NAME);
	// registry.registerBeanDefinition("requestContextImpl2",
	// reqCtxImpl2Bldr.getBeanDefinition());

    }

    @Bean
    @Scope("MultithreadedRequestScope")
    RequestContext requestContext() {
	return new RequestContextImpl();
    }
}
