package name.abhijitsarkar.javaee.common.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Abhijit Sarkar
 */
public class PublishedDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
    Pattern pattern = Pattern.compile("(?<rest>.+?[+-])(?<hr>\\d{1,2}?):(?<min>\\d{1,2}?)");

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        String publishedDateTime = p.getCodec().readValue(p, String.class);

        Matcher matcher = pattern.matcher(publishedDateTime);

        if (!matcher.matches() || matcher.groupCount() < 3) {
            throw new IOException("Failed to parse input token: " + publishedDateTime);
        }

        int hr = Integer.valueOf(matcher.group("hr"));
        int min = Integer.valueOf(matcher.group("min"));

        String correctlyFormattedPublishedDateTime = String.format("%s%s:%s",
                matcher.group("rest"),
                leftPadWithZeroIfNecessary(hr),
                leftPadWithZeroIfNecessary(min));

        return OffsetDateTime.parse(correctlyFormattedPublishedDateTime);
    }

    private String leftPadWithZeroIfNecessary(int str) {
        return String.format("%2s", str).replace(' ', '0');
    }
}
