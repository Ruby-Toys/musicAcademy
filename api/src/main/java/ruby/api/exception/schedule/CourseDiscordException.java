package ruby.api.exception.schedule;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class CourseDiscordException extends BusinessException {

    public static final String MESSAGE = "수강생과 선생님의 과목이 일치하지 않습니다.";

    public CourseDiscordException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
