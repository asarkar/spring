package org.abhijitsarkar.touchstone.condition

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.source.ConfigurationPropertySources
import org.springframework.core.env.Environment
import javax.annotation.PostConstruct

/**
 * @author Abhijit Sarkar
 */
class ConditionsExecutor(private val env: Environment) : Tasklet {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ConditionsExecutor::class.java)
        const val CONDITION_PHASE_KEY = "${Condition.PREFIX}.phase"
    }

    @Autowired(required = false)
    var conditions: List<Condition> = emptyList()

    private lateinit var props: Map<String, ConditionProperties>

    @PostConstruct
    fun postConstruct() {
        props = conditions
                .map { it.qualifiedName }
                .map { qn ->
                    LOGGER.debug("Attempting to bind condition: {}", qn)
                    val bindable = Bindable.of(ConditionProperties::class.java)
                    val properties = ConfigurationPropertySources.get(env)
                    qn to Binder(properties)
                            .bind(qn, bindable)
                            .orElse(ConditionProperties())
                }
                .toMap()
    }

    override fun execute(contribution: StepContribution?, chunkContext: ChunkContext): RepeatStatus {
        val phase = chunkContext.stepContext.stepExecution.executionContext[CONDITION_PHASE_KEY] as Condition.Phase

        LOGGER.info("Executing {} conditions", phase)

        return conditions
                .filter { it.phase() == phase }
                .map { it.qualifiedName to it }
                .filter { (qn, condition) ->
                    val shouldRun = props[qn]?.shouldRun ?: condition.shouldRun()
                    if (!shouldRun) {
                        LOGGER.info("Condition: {} is skipped", condition.qualifiedName)
                    }
                    shouldRun
                }
                .sortedBy { (qn, condition) ->
                    val order = props[qn]?.order ?: condition.order()
                    LOGGER.debug("Condition: {} has order: {}", condition.qualifiedName, order)
                    order
                }
                .map { (qn, condition) ->
                    val exitStatus = condition.run(chunkContext)
                    LOGGER.info("Condition: {} exited with: {}", qn, exitStatus)
                    exitStatus
                }
                .fold(ExitStatus.COMPLETED, ExitStatus::and)
                .let {
                    LOGGER.info("Combined exit status of all {} conditions: {}", phase, it)
                    RepeatStatus.FINISHED
                }
    }

    class ConditionProperties {
        var order: Int? = null
        var shouldRun: Boolean? = null
    }
}