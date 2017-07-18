package org.abhijitsarkar

import org.abhijitsarkar.domain.License

/**
 * @author Abhijit Sarkar
 */
data class LicenseGeneratedEvent(val licenses: Map<String, Collection<License>>)