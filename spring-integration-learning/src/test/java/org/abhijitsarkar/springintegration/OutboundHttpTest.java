package org.abhijitsarkar.springintegration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"inbound-default", "outbound-http"})
public class OutboundHttpTest {
    @Test
    public void fileWritten() throws InterruptedException {
        Thread.sleep(20000l);
    }
}
