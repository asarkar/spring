Touchstone
===
*A test or criterion for determining the quality or genuineness of a thing*

- To execute [TouchstoneDemoTest](touchstone-demo/src/main/kotlin/TouchstoneDemoTest.kt), run [TouchstoneDemoApplication](touchstone-demo/src/main/kotlin/TouchstoneDemoApplication.kt)
with `-Dtouchstone.junit.selectClass=org.abhijitsarkar.touchstone.demo.TouchstoneDemoTest`
- To run using external H2 DB, run with `-Dspring.datasource.url='jdbc:h2:~/test;AUTO_SERVER=true'`
- To change the test executor, run with `-Dtouchstone.testExecutor=[GRADLE|JUNIT]`
- To skip voting, run with `-Dtouchstone.vote.skip=true`
