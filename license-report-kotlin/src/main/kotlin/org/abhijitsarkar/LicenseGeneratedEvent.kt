package org.abhijitsarkar

import org.abhijitsarkar.domain.License

/**
 * @author Abhijit Sarkar
 */
data class LicenseGeneratedEvent(val licenses: MutableMap<String, Collection<License>>)