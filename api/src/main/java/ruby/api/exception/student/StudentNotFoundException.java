package ruby.api.exception.student;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class StudentNotFoundException extends BusinessException {

    public static final String MESSAGE = "해당 아이디로 등록된 수강생이 존재하지 않습니다.";

    public StudentNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
