package ruby.api.valid.validator;

import ruby.api.valid.NamePattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<NamePattern, String> {

    private static final String NAME_REGEXP = "^[가-힣a-zA-Z\\d]{2,20}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(NAME_REGEXP);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
