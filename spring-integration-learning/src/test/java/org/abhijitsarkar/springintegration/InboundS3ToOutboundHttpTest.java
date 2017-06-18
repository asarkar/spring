package org.abhijitsarkar.springintegration;

import org.abhijitsarkar.springintegration.http.HttpProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Abhijit Sarkar
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"inbound-s3", "outbound-http"})
public class InboundS3ToOutboundHttpTest {
    @Autowired
    private HttpProperties httpProperties;

    @Test
    public void fileWritten() throws InterruptedException {
        Thread.sleep(httpProperties.getReadTimeout());
    }
}
