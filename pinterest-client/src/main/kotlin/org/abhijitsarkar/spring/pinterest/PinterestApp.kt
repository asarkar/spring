package org.abhijitsarkar.spring.pinterest

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
class PinterestApp {
    @Value("\${pinterest.cacheSpec}")
    lateinit var cacheSpec: String

    @Bean
    fun cache() = CaffeineCacheManager()
            .apply { setCacheSpecification(cacheSpec) }
            .getCache("pinterest")
}

fun main(args: Array<String>) {
    SpringApplication.run(PinterestApp::class.java, *args)
}




