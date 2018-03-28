package org.abhijitsarkar.touchstone.condition

import org.abhijitsarkar.touchstone.TouchstoneAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration
import org.springframework.boot.runApplication

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication(exclude = [
    TouchstoneAutoConfiguration::class,
    BatchAutoConfiguration::class,
    DataSourceAutoConfiguration::class,
    DataSourceTransactionManagerAutoConfiguration::class,
    HibernateJpaAutoConfiguration::class,
    JacksonAutoConfiguration::class,
    JdbcTemplateAutoConfiguration::class,
    JpaRepositoriesAutoConfiguration::class,
    JtaAutoConfiguration::class,
    PersistenceExceptionTranslationAutoConfiguration::class,
    TransactionAutoConfiguration::class,
    CacheAutoConfiguration::class,
    ReactiveSecurityAutoConfiguration::class,
    EmbeddedWebServerFactoryCustomizerAutoConfiguration::class,
    ProjectInfoAutoConfiguration::class
])
class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}