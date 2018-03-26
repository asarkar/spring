package org.abhijitsarkar.touchstone.precondition

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.ThreadPoolDispatcher
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeout
import org.abhijitsarkar.touchstone.TestPreconditionVoter
import org.abhijitsarkar.touchstone.TouchstoneProperties
import org.abhijitsarkar.touchstone.Vote
import org.abhijitsarkar.touchstone.VoteCastingStrategy.PARALLEL
import org.abhijitsarkar.touchstone.VoteCountingStrategy.AFFIRMATIVE
import org.abhijitsarkar.touchstone.VoteCountingStrategy.CONSENSUS
import org.abhijitsarkar.touchstone.VoteCountingStrategy.UNANIMOUS
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.CollectionUtils.isEmpty
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Abhijit Sarkar
 */
class PreconditionFailedException(override val message: String? = null, override val cause: Throwable? = null) : RuntimeException(message, null)

class Teller(touchstoneProperties: TouchstoneProperties) : Tasklet {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(Teller::class.java)
    }

    private val vote = touchstoneProperties.vote

    @Autowired(required = false)
    var voters: List<TestPreconditionVoter> = emptyList()

    override fun execute(contribution: StepContribution?, chunkContext: ChunkContext?): RepeatStatus {
        return if (go())
            RepeatStatus.FINISHED
        else
            throw PreconditionFailedException()
    }

    private fun go(): Boolean {
        if (isEmpty(voters)) {
            LOGGER.warn("No voters found!")
            return true
        }

        val context = ConcurrentHashMap<String, Any>()
        val dispatcher =
                if (vote.castingStrategy == PARALLEL)
                    CommonPool
                else
                    newSingleThreadContext("touchstone.vote")

        val voteMap = runBlocking {
            try {
                voters
                        .map { async(dispatcher) { it.vote(context) } }
                        .groupBy { withTimeout(vote.timeoutMillis) { it.await() } }
                        .mapValues { it.value.size }
            } catch (e: TimeoutCancellationException) {
                throw PreconditionFailedException(cause = e)
            } finally {
                if (vote.castingStrategy != PARALLEL)
                    (dispatcher as? ThreadPoolDispatcher)?.close()
            }
        }

        LOGGER.debug("Vote map: {}", voteMap)
        LOGGER.debug("Vote counting strategy: {}", vote.countingStrategy)

        val numReady = voteMap[Vote.READY] ?: 0

        LOGGER.info("Number of ready votes: {}", numReady)

        return when (vote.countingStrategy) {
            UNANIMOUS -> !voteMap.containsKey(Vote.NOT_READY) && !voteMap.containsKey(Vote.ABSTAIN)
            AFFIRMATIVE -> numReady > 0
            CONSENSUS -> numReady >= vote.quorum
        }
    }
}