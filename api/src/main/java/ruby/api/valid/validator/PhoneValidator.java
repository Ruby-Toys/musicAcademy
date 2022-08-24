package ruby.api.valid.validator;

import ruby.api.valid.PhonePattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<PhonePattern, String> {

    private static final String PHONE_REGEXP = "^(010|011|016|017|019)-\\d{3,4}-\\d{4}$";
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(PHONE_REGEXP);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
