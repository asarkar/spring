### Interesting classes:

* `FeignClientsConfiguration`

* `LoadBalancerFeignClient` - interestingly, it doesn't delegate to `OkHttpLoadBalancingClient` 
  (see https://github.com/spring-cloud/spring-cloud-netflix/issues/1967). Same is true for the retryable versions.

* `RibbonClientConfiguration` - what the name says.

* `FeignAutoConfiguration` and `FeignRibbonClientAutoConfiguration`

* `DefaultClientConfigImpl`

### Questions:

1. What's the Hystrix group key used by a Spring Cloud Feign client, since the `@FeignClient` annotation doesn't have 
   an attribute to specify it?
   
   *Ans*: Same as the Feign client name. The relevant code begins in `FeignClientsConfiguration.HystrixFeignConfiguration`.

2. What's the Hystrix thread pool key used by a Spring Cloud Feign client, since the `@FeignClient` annotation doesn't have 
   an attribute to specify it?
   
   *Ans*: Same as the Feign client name. The relevant code begins in `FeignClientsConfiguration.HystrixFeignConfiguration`.

3. What's the command key used by a Spring Cloud Feign client, since the `@FeignClient` annotation doesn't have 
   an attribute to specify it?
   
   *Ans*: `<SimpleClassName>#<methodName>()` (the parentheses at the end are included).


