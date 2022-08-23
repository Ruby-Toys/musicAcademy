package ruby.api.valid.validator;

import ruby.api.valid.DatePattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateValidator implements ConstraintValidator<DatePattern, String> {

    private static final String DATE_REGEXP = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12]\\d|3[01])";


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(DATE_REGEXP);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
