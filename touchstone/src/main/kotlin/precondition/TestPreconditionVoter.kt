package org.abhijitsarkar.touchstone.precondition

/**
 * @author Abhijit Sarkar
 */
enum class Vote {
    READY, NOT_READY, ABSTAIN;
}

interface TestPreconditionVoter {
    fun vote(context: Map<String, Any>): Vote
}