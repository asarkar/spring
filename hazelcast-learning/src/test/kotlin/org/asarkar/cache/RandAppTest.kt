package org.asarkar.cache

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNotEqualTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate

@SpringBootTest(classes = [RandApp::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RandAppTest(@Autowired val restTemplate: TestRestTemplate) {
    @Test
    fun testRandApp() {
        val rands = (1..5)
            .map { restTemplate.getForObject("/", Int::class.java) }
            .toSet()
        assertThat(rands).hasSize(1)

        restTemplate.delete("/")
        val rand2 = restTemplate.getForObject("/", Int::class.java)
        assertThat(rands.first()).isNotEqualTo(rand2)
    }
}