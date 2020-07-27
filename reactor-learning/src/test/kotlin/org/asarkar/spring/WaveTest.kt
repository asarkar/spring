package org.asarkar.spring

import org.junit.jupiter.api.Test

class WaveTest {
    @Test
    fun testCanReadHeader() {
        println(WaveHeader.fromPath("/blues2.wav"))
    }
}