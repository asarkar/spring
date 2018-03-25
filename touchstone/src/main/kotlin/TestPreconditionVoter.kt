package org.abhijitsarkar.touchstone

/**
 * @author Abhijit Sarkar
 */
enum class Vote {
    READY, NOT_READY, ABSTAIN;
}

enum class VoteCountingStrategy {
    UNANIMOUS, CONSENSUS, AFFIRMATIVE;
}

enum class VoteCastingStrategy {
    SERIAL, PARALLEL
}

interface TestPreconditionVoter {
    fun vote(context: Map<String, Any>): Vote
}