package org.asarkar.cache

import org.asarkar.cache.RandApp.Companion.RAND_CACHE
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RandController(val randService: RandService, val cacheManager: CacheManager) {
    private val logger = LoggerFactory.getLogger(RandController::class.java)

    @GetMapping("/")
    fun rand(): Int {
        return randService.rand()
    }

    @DeleteMapping("/")
    fun clear() {
        logger.info("Flushing cache: {}", RAND_CACHE)
        cacheManager.getCache(RAND_CACHE)?.clear()
    }
}