package ruby.api.valid.validator;

import ruby.api.valid.EmailPattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<EmailPattern, String> {
    private static final String EMAIL_REGEXP = "^[a-zA-Z\\d_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z\\d.-]+$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(EMAIL_REGEXP);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
