package org.abhijitsarkar.touchstone.precondition

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.ThreadPoolDispatcher
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeout
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

class Teller(private val vote: VotingProperties) : Tasklet {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(Teller::class.java)
    }

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
                if (vote.castingStrategy == VoteCastingStrategy.PARALLEL)
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
                if (vote.castingStrategy != VoteCastingStrategy.PARALLEL)
                    (dispatcher as? ThreadPoolDispatcher)?.close()
            }
        }

        LOGGER.debug("Vote map: {}", voteMap)
        LOGGER.debug("Vote counting strategy: {}", vote.countingStrategy)

        val numReady = voteMap[Vote.READY] ?: 0

        LOGGER.info("Number of ready votes: {}", numReady)

        return when (vote.countingStrategy) {
            VoteCountingStrategy.UNANIMOUS -> !voteMap.containsKey(Vote.NOT_READY) && !voteMap.containsKey(Vote.ABSTAIN)
            VoteCountingStrategy.AFFIRMATIVE -> numReady > 0
            VoteCountingStrategy.CONSENSUS -> numReady >= vote.quorum
        }
    }
}