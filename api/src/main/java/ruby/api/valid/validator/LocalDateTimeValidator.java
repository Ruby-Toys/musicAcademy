package ruby.api.valid.validator;

import ruby.api.valid.LocalDateTimePattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalDateTimeValidator implements ConstraintValidator<LocalDateTimePattern, String> {

    private static final String LOCAL_DATE_TIME_REGEXP
            = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12]\\d|3[01]) ([01]\\d|2[0-3]):(0\\d|[1-5]\\d):(0\\d|[1-5]\\d)";


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(LOCAL_DATE_TIME_REGEXP);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
