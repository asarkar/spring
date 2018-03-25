package org.abhijitsarkar.touchstone.precondition

import org.abhijitsarkar.touchstone.TestPreconditionVoter
import org.abhijitsarkar.touchstone.TouchstoneProperties
import org.abhijitsarkar.touchstone.Vote
import org.abhijitsarkar.touchstone.VoteCastingStrategy
import org.abhijitsarkar.touchstone.VoteCountingStrategy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.repeat.RepeatStatus

/**
 * @author Abhijit Sarkar
 */
class TellerTest {
    private val readyVoter = object : TestPreconditionVoter {
        override fun vote(context: Map<String, Any>): Vote {
            println("Running on thread: ${Thread.currentThread().name}")
            return Vote.READY
        }
    }
    private val notReadyVoter = object : TestPreconditionVoter {
        override fun vote(context: Map<String, Any>): Vote {
            println("Running on thread: ${Thread.currentThread().name}")
            return Vote.NOT_READY
        }
    }
    private val abstainVoter = object : TestPreconditionVoter {
        override fun vote(context: Map<String, Any>): Vote {
            println("Running on thread: ${Thread.currentThread().name}")
            return Vote.ABSTAIN
        }
    }

    private val voters = listOf(readyVoter, notReadyVoter, abstainVoter)
    private var touchstoneProperties = TouchstoneProperties()
    private val teller = Teller(touchstoneProperties)

    @BeforeEach
    fun beforeEach() {
        teller.voters = voters
    }

    @AfterEach
    fun afterEach() {
        touchstoneProperties.vote = TouchstoneProperties.VotingProperties()
    }

    @Test
    fun `should throw exception when not unanimous`() {
        touchstoneProperties.vote.countingStrategy = VoteCountingStrategy.UNANIMOUS
        assertThrows(PreconditionFailedException::class.java, { teller.execute(null, null) })
    }

    @Test
    fun `should complete when one ready vote`() {
        touchstoneProperties.vote.countingStrategy = VoteCountingStrategy.AFFIRMATIVE
        val status = teller.execute(null, null)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)
    }

    @Test
    fun `should complete when quorum is reached`() {
        touchstoneProperties.vote.countingStrategy = VoteCountingStrategy.CONSENSUS
        touchstoneProperties.vote.quorum = 1
        val status = teller.execute(null, null)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)
    }

    @Test
    fun `should throw exception when quorum is not reached`() {
        touchstoneProperties.vote.countingStrategy = VoteCountingStrategy.CONSENSUS
        touchstoneProperties.vote.quorum = 3
        assertThrows(PreconditionFailedException::class.java, { teller.execute(null, null) })
    }

    @Test
    fun `should cast votes parallelly`() {
        touchstoneProperties.vote.castingStrategy = VoteCastingStrategy.PARALLEL
        touchstoneProperties.vote.countingStrategy = VoteCountingStrategy.AFFIRMATIVE
        val status = teller.execute(null, null)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)
    }

    @Test
    fun `should complete when no voters`() {
        teller.voters = emptyList()

        val status = teller.execute(null, null)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)
    }
}