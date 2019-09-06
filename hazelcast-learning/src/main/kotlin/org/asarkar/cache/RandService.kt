package org.asarkar.cache

import org.asarkar.cache.RandApp.Companion.RAND_CACHE
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.cache.annotation.CacheResult
import kotlin.random.Random

interface RandService {
    @CacheResult(cacheName = RAND_CACHE)
    fun rand(): Int
}

@Service
class RandServiceImpl : RandService {
    private val logger = LoggerFactory.getLogger(RandServiceImpl::class.java)

    override fun rand(): Int {
        logger.info("Generating random number")
        return Random.nextInt()
    }
}
