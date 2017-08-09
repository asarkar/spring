package org.abhijitsarkar.demo

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

/**
 * @author Abhijit Sarkar
 */
class SequenceTest : ShouldSpec() {
    init {
        var counter = 1

        fun supplier(): Int = if (counter <= 3) counter++ else throw IllegalArgumentException("test")

        should("evaluate sequence lazily") {
            val item = generateSequence { supplier() }.find { it % 2 == 0 }
            item shouldBe 2
        }
    }
}