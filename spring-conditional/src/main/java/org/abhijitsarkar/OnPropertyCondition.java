package org.abhijitsarkar;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Abhijit Sarkar
 */
public class OnPropertyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Map<String, Object> attributes = annotatedTypeMetadata
                .getAnnotationAttributes(ConditionalOnProperty.class.getName());
        String value = (String) attributes.get("value");

        String name = conditionContext.getEnvironment().getProperty("greeting");

        return !isEmpty(name) && name.equalsIgnoreCase(value);
    }
}
