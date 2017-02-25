package org.abhijitsarkar;

import java.io.File;

/**
 * @author Abhijit Sarkar
 */
public class NativeApplicationContextInitializer implements TestApplicationContextInitializer {
    @Override
    public String getBaseUri(File resources) {
        return resources.getAbsolutePath();
    }

    @Override
    public String getOutput() {
        return "native.yaml";
    }
}
