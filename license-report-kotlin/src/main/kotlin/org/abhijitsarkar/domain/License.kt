package org.abhijitsarkar.domain

/**
 * @author Abhijit Sarkar
 */
data class License(
        val name: String = "",
        val url: String = "",
        val components: List<String> = emptyList(),
        val valid: Boolean = false
)