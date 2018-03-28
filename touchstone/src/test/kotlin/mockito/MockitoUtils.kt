package org.abhijitsarkar.touchstone.mockito

import org.mockito.Mockito

/**
 * @author Abhijit Sarkar
 */
fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}

@Suppress("UNCHECKED_CAST")
fun <T> uninitialized(): T = null as T
