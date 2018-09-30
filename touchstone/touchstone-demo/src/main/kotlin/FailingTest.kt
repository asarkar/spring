package org.abhijitsarkar.touchstone.demo

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * @author Abhijit Sarkar
 */
class FailingTest {
    @Test
    @DisplayName("should fail")
    fun test2() {
        assertTrue(false)
    }
}