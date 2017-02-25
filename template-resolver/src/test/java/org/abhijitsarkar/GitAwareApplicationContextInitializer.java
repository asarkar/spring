package org.abhijitsarkar;

import java.io.File;

/**
 * @author Abhijit Sarkar
 */
public class GitAwareApplicationContextInitializer implements TestApplicationContextInitializer {
    @Override
    public String getBaseUri(File resources) {
        return resources.toURI().toString();
    }

    @Override
    public String getOutput() {
        return "git.yaml";
    }
}
