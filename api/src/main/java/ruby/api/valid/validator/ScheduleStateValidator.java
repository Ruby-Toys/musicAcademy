package ruby.api.valid.validator;

import ruby.api.valid.ScheduleStatePattern;
import ruby.core.domain.enums.ScheduleState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleStateValidator implements ConstraintValidator<ScheduleStatePattern, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(getRegexpCourse());
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static String getRegexpCourse() {
        ScheduleState[] scheduleStates = ScheduleState.values();
        StringBuilder builder = new StringBuilder();
        for (ScheduleState state : scheduleStates) {
            builder.append(state.name()).append("|");
        }
        return builder.substring(0, builder.length());
    }
}
