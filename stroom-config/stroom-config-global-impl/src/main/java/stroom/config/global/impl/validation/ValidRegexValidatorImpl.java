package stroom.config.global.impl.validation;

import stroom.util.shared.ValidRegexValidator;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ValidRegexValidatorImpl implements ValidRegexValidator {

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        boolean result = true;

        try {
            Pattern.compile(value);
        } catch (PatternSyntaxException e) {
            final String msgTemplate =
                context.getDefaultConstraintMessageTemplate() +
                    ". caused by: " +
                    e.getMessage().replaceAll("\\n"," ");

            // We want the exception details in the message so bin the default constraint
            // violation and make a new one.
            context.disableDefaultConstraintViolation();
            context
                .buildConstraintViolationWithTemplate(msgTemplate)
                .addConstraintViolation();
            result = false;
        }
        return result;
    }
}
