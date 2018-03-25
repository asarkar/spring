package org.abhijitsarkar.touchstone.precondition

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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Abhijit Sarkar
 */
class PreconditionFailedException(override val message: String? = null) : RuntimeException(message)

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

        val voteMap = Flux.fromIterable(voters)
                .flatMap {
                    val vote = Mono.fromCallable { it.vote(context) }
                    if (this.vote.castingStrategy == PARALLEL) {
                        // parallel is tailored for parallelization of tasks for performance purposes,
                        // and dispatching of work between "rails" or "groups",
                        // each of which get their own execution context from the Scheduler you pass to runOn.
                        // In short, it will put all your CPU cores to work if you do CPU intensive work.
                        // But you're doing I/O bound work, flatMap is a better candidate.
                        // That use of flatMap for parallelization is more about orchestration.
                        vote.subscribeOn(Schedulers.newParallel("touchstone.vote"))
                    } else vote
                }
                .timeout(Duration.ofMillis(vote.timeoutMillis))
                .collectList()
                .block()
                ?.groupingBy { it }?.eachCount() ?: emptyMap()

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