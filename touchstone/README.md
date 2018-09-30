Touchstone
===
*A test or criterion for determining the quality or genuineness of a thing*

Touchstone is a simple *integration test framework* that uses the same basic idea as JUnit, but is *designed for modularity*
 not easily achievable with JUnit when the tests need to *interact with external dependencies*.

Let's look at each of italicized phrases above in detail:

1. **Integration test framework** - The tests written using Touchstone are Spring Boot applications.
2. **Designed for modularity** - Unlike JUnit, where the `@Before` and `@After` steps must be part of the test class,
   Touchstone extends the idea of pre and post testing steps using [Condition](src/main/kotlin/condition/Condition.kt).
   A `Condition` has a phase (`pre` or `post`), and an order (positive integer). Preconditions run before the tests do
   (duh), and postconditions after. Tests that depend on external dependencies usually require complex setup and cleanup
   steps, and instead of cramming them all in one class, `Condition`s allow for them to modularized. Heck, you can even
   test your `Condition`s if you're so inclined.

   A `Condition` can be disabled using externalized properties, as well as its order can be changed. The syntax is as
   follows:
   ```
   touchstone.condition.<phase>.<fully-qualified-classname>.should-run=[true|false]
   touchstone.condition.<phase>.<fully-qualified-classname>.order=<positive integer>
   ```
   > * Replace special characters in the classname (`$` for anonymous classes) with `-`.
   > * Tests are skipped if any of the preconditions fails. Postconditions are always executed.
   > * If there are multiple pre/post conditions, all of them are executed. If you wish to skip a condition based on the
       exit status of the previous condition, you may retrieve the last exit status from the `ChunkContext`, like so:
       `chunkContext.stepContext.stepExecution.exitStatus`.
       The name of the last executed condition is also available from the `ExecutionContext` map (`chunkContext.stepContext.stepExecution.executionContext`)
       against the key `touchstone.condition.execution.last`.
3. **Interact with external dependencies** - If your test setup/cleanup is trivial to simple, you're probably better off
   sticking with JUnit. But if you need to do things like insert mock data in the database and clean up afterwards,
   you'll benefit from Touchstone's modular design.

### Other Features

   * **Choice between JUnit test executor and Gradle executor** - The tests may be executed by the JUnit 5 Jupiter engine
   (default), or by Gradle. When using the former, the test results are persisted in a H2 database, that can run in an embedded
   mode (default), or externally. When using the Gradle executor, test execution can be configured as usual in the
   `build.gradle`.
   See [JUnitProperties.kt](src/main/kotlin/execution/junit/JUnitProperties.kt)
   and [GradleProperties.kt](src/main/kotlin/execution/gradle/GradleProperties.kt)
   for a list of properties supported by the respective executors that can be fully externalized using regular Spring
   Boot property management.

   * To run using external H2 DB, run with `-Dspring.datasource.url='jdbc:h2:~/test;AUTO_SERVER=true'`
   * To change the test executor, run with `-Dtouchstone.test-executor=[GRADLE|JUNIT]`

### Demo Application
Run [DemoApplication](touchstone-demo/src/main/kotlin/DemoApplication.kt) to simulate the following use cases:

| Preconditions | Tests      | Postconditions | JVM Options                                                                                                                                       |
|---------------|------------|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| Successful    | Successful | Successful     | -Dtouchstone.junit.select-class=org.abhijitsarkar.touchstone.demo.PassingTest -Dtouchstone.condition.pre.failing-pre-condition-1.should-run=false |
| Failed        | Skipped    | Successful     | -Dtouchstone.junit.select-class=DoesNotExist         |
| Successful    | Failed     | Successful     | -Dtouchstone.junit.select-class=org.abhijitsarkar.touchstone.demo.FailingTest -Dtouchstone.condition.pre.failing-pre-condition-1.should-run=false |

### Languages/Frameworks

  * [Kotlin](https://kotlinlang.org/) - Touchstone is written in Kotlin but your tests don't need to be.
  * [Spring Boot](https://spring.io/projects/spring-boot)
  * [Spring Batch](https://spring.io/projects/spring-batch)
  * [JUnit 5](https://junit.org/junit5/) - Touchstone is intended to be used with JUnit 5 tests. JUnit 4 may theoretically
    be supported, but don't look at me.
  * [Gradle Tooling API](https://docs.gradle.org/current/userguide/embedding.html)
  * [Spring Data JPA](https://projects.spring.io/spring-data-jpa/) - For persisting test results to a database.
  * [H2 database](http://www.h2database.com/html/main.html)

