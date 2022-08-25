package ruby.api.exception.teacher;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class TeacherNotFoundException extends BusinessException {

    public static final String MESSAGE = "해당 선생님이 존재하지 않습니다.";

    public TeacherNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
