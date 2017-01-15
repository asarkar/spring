package org.abhijitsarkar.ig;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

/**
 * @author Abhijit Sarkar
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = DEFINED_PORT,
        properties = "server.port=8080"
)
@ActiveProfiles({"restOperations", "test"})
public class RestOperationsIgServiceTest extends AbstractIgServiceTest {
}
