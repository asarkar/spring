package name.abhijitsarkar.javaee.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Abhijit Sarkar
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestConfig.class, CommonConfig.class})
public class CacheIntegrationTest {
    @Autowired
    private RandomNumberGenerator generator;

    @Test
    public void testCache() {
        Integer random = generator.random(10);

        IntStream.range(1, 10).forEach(i -> assertEquals(random, generator.random(10)));
    }
}
