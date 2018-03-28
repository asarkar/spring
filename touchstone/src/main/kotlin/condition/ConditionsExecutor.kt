package org.abhijitsarkar.touchstone.condition

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.MutablePropertyValues
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.validation.BindException
import org.springframework.validation.DataBinder
import javax.annotation.PostConstruct

/**
 * @author Abhijit Sarkar
 */
class ConditionsExecutor(private val phase: Condition.Phase, private val env: ConfigurableEnvironment) : Tasklet {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ConditionsExecutor::class.java)
        private const val CONDITION_PREFIX = "touchstone.condition"
    }

    @Autowired(required = false)
    var conditions: List<Condition> = emptyList()

    private lateinit var props: Map<String, ConditionProperties>

    @PostConstruct
    fun postConstruct() {
        val map = env.propertySources
                .filter { it is EnumerablePropertySource }
                .flatMap { (it as EnumerablePropertySource).propertyNames.toList() }
                .filter { it.startsWith(CONDITION_PREFIX) }
                .map { it to env.getProperty(it, Any::class.java) }
                .toMap()

        props = conditions
                .map { it.qualifiedName }
                .map { qn ->
                    qn to map
                            .filter { it.key.startsWith(qn) }
                            .mapKeys { it.key.split("$qn.")[1] }
                            .let { MutablePropertyValues(it) }
                }
                .map { (qn, properties) ->
                    qn to ConditionProperties().apply {
                        val binder = DataBinder(this)
                        try {
                            binder.bind(properties)
                        } catch (e: BindException) {
                            LOGGER.error("Failed to bind properties for condition: $qn", e)
                        } finally {
                            binder.close()
                        }
                    }
                }
                .toMap()
    }

    override fun execute(contribution: StepContribution?, chunkContext: ChunkContext): RepeatStatus {
        return conditions
                .filter { it.phase() == phase }
                .map { it.qualifiedName to it }
                .filter { (qn, condition) ->
                    props[qn]?.shouldRun ?: condition.shouldRun()
                }
                .sortedBy { (qn, condition) ->
                    props[qn]?.order ?: condition.order()
                }
                .map { (qn, condition) ->
                    val exitStatus = condition.run(chunkContext)
                    LOGGER.info("Condition: {} exited with: {}", qn, exitStatus)
                    exitStatus
                }
                .fold(ExitStatus.COMPLETED, ExitStatus::and)
                .let {
                    LOGGER.info("Combined exit status: {}", it)
                    RepeatStatus.FINISHED
                }
    }

    class ConditionProperties {
        var order: Int? = null
        var shouldRun: Boolean? = null
    }
}