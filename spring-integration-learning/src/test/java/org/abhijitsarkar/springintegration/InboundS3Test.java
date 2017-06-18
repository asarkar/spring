package org.abhijitsarkar.springintegration;

import org.abhijitsarkar.springintegration.http.HttpProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"inbound-s3", "outbound-default"})
public class InboundS3Test {
    @Autowired
    private HttpProperties httpProperties;

    @Test
    public void fileWritten() throws InterruptedException {
        Thread.sleep(5l);
    }
}
