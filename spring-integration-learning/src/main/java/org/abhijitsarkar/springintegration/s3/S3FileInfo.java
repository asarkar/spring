package org.abhijitsarkar.springintegration.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FilenameUtils;

import java.io.UncheckedIOException;

/**
 * @author Abhijit Sarkar
 */
public class S3FileInfo extends org.springframework.integration.aws.support.S3FileInfo {
    private static final ObjectWriter OBJECT_WRITER = new ObjectMapper().writerFor(S3ObjectSummary.class);

    public S3FileInfo(S3ObjectSummary s3ObjectSummary) {
        super(s3ObjectSummary);
    }

    @Override
    public String getFilename() {
        return FilenameUtils.getName(super.getFilename());
    }

    @Override
    public String toJson() {
        try {
            return OBJECT_WRITER.writeValueAsString(super.getFileInfo());
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
