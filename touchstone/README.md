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

   Currently, all the preconditions must succeed for the tests to run. However, using Spring profiles, JUnit 5 test
   selection, and the ability to disable conditions externally, you can come up with almost arbitrary combinations of
   `Condition`s and tests. A `Condition` must be a Spring bean - there are no other rules. You are free to implement
   it however you like, using whatever libraries you like.
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

### Sample App

- To execute [TouchstoneDemoTest](touchstone-demo/src/main/kotlin/TouchstoneDemoTest.kt), run [TouchstoneDemoApplication](touchstone-demo/src/main/kotlin/TouchstoneDemoApplication.kt)
with `-Dtouchstone.junit.select-class=org.abhijitsarkar.touchstone.demo.TouchstoneDemoTest`

### Languages/Frameworks

  * [Kotlin](https://kotlinlang.org/) - Touchstone is written in Kotlin but your tests don't need to be.
  * [Spring Boot](https://spring.io/projects/spring-boot)
  * [Spring Batch](https://spring.io/projects/spring-batch)
  * [JUnit 5](https://junit.org/junit5/) - Touchstone is intended to be used with JUnit 5 tests. JUnit 4 may theoretically
    be supported, but don't look at me.
  * [Gradle Tooling API](https://docs.gradle.org/current/userguide/embedding.html)
  * [Spring Data JPA](https://projects.spring.io/spring-data-jpa/) - For persisting test results to a database.
  * [H2 database](http://www.h2database.com/html/main.html)

