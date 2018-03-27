package org.abhijitsarkar.touchstone.precondition

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.batch.repeat.RepeatStatus
import java.lang.Thread.sleep

/**
 * @author Abhijit Sarkar
 */
class TellerTest {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TellerTest::class.java)
    }

    private val readyVoter = object : TestPreconditionVoter {
        override fun vote(context: Map<String, Any>): Vote {
            sleep(1000)
            return Vote.READY.apply {
                LOGGER.info("{}", this)
            }
        }
    }
    private val notReadyVoter = object : TestPreconditionVoter {
        override fun vote(context: Map<String, Any>): Vote {
            sleep(1000)
            return Vote.NOT_READY.apply {
                LOGGER.info("{}", this)
            }
        }
    }
    private val abstainVoter = object : TestPreconditionVoter {
        override fun vote(context: Map<String, Any>): Vote {
            sleep(1000)
            return Vote.ABSTAIN.apply {
                LOGGER.info("{}", this)
            }
        }
    }

    private val voters = listOf(readyVoter, notReadyVoter, abstainVoter)
    private var vote = VotingProperties()
    private val teller = Teller(vote)

    @BeforeEach
    fun beforeEach() {
        teller.voters = voters
    }

    @AfterEach
    fun afterEach() {
        vote = VotingProperties()
    }

    @Test
    fun `should throw exception when not unanimous`() {
        vote.countingStrategy = VoteCountingStrategy.UNANIMOUS
        assertThrows(PreconditionFailedException::class.java, { teller.execute(null, null) })
    }

    @Test
    fun `should complete when one ready vote`() {
        vote.countingStrategy = VoteCountingStrategy.AFFIRMATIVE
        val status = teller.execute(null, null)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)
    }

    @Test
    fun `should complete when quorum is reached`() {
        vote.countingStrategy = VoteCountingStrategy.CONSENSUS
        vote.quorum = 1
        val status = teller.execute(null, null)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)
    }

    @Test
    fun `should throw exception when quorum is not reached`() {
        vote.countingStrategy = VoteCountingStrategy.CONSENSUS
        vote.quorum = 3
        assertThrows(PreconditionFailedException::class.java, { teller.execute(null, null) })
    }

    @Test
    fun `should cast votes parallelly`() {
        vote.castingStrategy = VoteCastingStrategy.PARALLEL
        vote.countingStrategy = VoteCountingStrategy.AFFIRMATIVE
        val status = teller.execute(null, null)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)
    }

    @Test
    fun `should timeout`() {
        vote.timeoutMillis = 100
        vote.countingStrategy = VoteCountingStrategy.AFFIRMATIVE
        assertThrows(PreconditionFailedException::class.java, { teller.execute(null, null) })
    }

    @Test
    fun `should complete when no voters`() {
        teller.voters = emptyList()

        val status = teller.execute(null, null)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)
    }
}