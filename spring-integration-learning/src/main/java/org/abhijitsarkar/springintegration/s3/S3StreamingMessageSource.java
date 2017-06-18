package org.abhijitsarkar.springintegration.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.integration.file.remote.AbstractFileInfo;
import org.springframework.integration.file.remote.RemoteFileTemplate;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Abhijit Sarkar
 */
public class S3StreamingMessageSource extends org.springframework.integration.aws.inbound.S3StreamingMessageSource {
    public S3StreamingMessageSource(RemoteFileTemplate<S3ObjectSummary> template) {
        super(template, null);
    }

    public S3StreamingMessageSource(RemoteFileTemplate<S3ObjectSummary> template,
                                    Comparator<AbstractFileInfo<S3ObjectSummary>> comparator) {
        super(template, comparator);
    }

    @Override
    protected List<AbstractFileInfo<S3ObjectSummary>> asFileInfoList(Collection<S3ObjectSummary> collection) {
        return collection.stream()
                .map(S3FileInfo::new)
                .collect(toList());
    }
}
