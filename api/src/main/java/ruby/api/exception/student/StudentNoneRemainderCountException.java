package ruby.api.exception.student;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class StudentNoneRemainderCountException extends BusinessException {

    public static final String MESSAGE = "스케줄을 예약하기 위해서는 추가 결제가 필요합니다.";

    public StudentNoneRemainderCountException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
