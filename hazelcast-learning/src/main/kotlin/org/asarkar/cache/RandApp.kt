package org.asarkar.cache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class RandApp {
    companion object {
        const val RAND_CACHE = "rand-cache"
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<RandApp>(*args)
        }
    }
}