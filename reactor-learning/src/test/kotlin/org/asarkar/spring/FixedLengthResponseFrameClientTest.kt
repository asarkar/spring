package org.asarkar.spring

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FixedLengthResponseFrameClientTest {
    @Test
    fun testHonorsMaxChunkSize() {
        val maxChunkSize = 4096
        val chunks = FixedLengthResponseFrameClient.get(
            "https://uweb.engr.arizona.edu/~429rns/audiofiles/blues2.wav", maxChunkSize
        )

        assertThat(chunks.subList(0, chunks.size - 1))
            .allMatch { it.size ==  maxChunkSize}
        assertThat(chunks.last().size).isLessThanOrEqualTo(maxChunkSize)
    }
}