package org.abhijitsarkar.touchstone.demo

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * @author Abhijit Sarkar
 */
class PassingTest {
    @Test
    @DisplayName("should pass")
    fun test1() {
        assertTrue(true)
    }
}