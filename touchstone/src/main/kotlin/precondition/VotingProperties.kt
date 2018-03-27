package org.abhijitsarkar.touchstone.precondition

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Abhijit Sarkar
 */
enum class VoteCountingStrategy {
    UNANIMOUS, CONSENSUS, AFFIRMATIVE;
}

enum class VoteCastingStrategy {
    SERIAL, PARALLEL
}

@ConfigurationProperties("touchstone.vote")
@Component
class VotingProperties {
    var countingStrategy = VoteCountingStrategy.UNANIMOUS
    var castingStrategy = VoteCastingStrategy.SERIAL
    var quorum = 1
    var timeoutMillis = 5000L
    var skip = false
}