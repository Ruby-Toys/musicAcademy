package ruby.api.exception.schedule;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class ScheduleNotEqualsDayException extends BusinessException {

    public static final String MESSAGE = "시작 날짜와 종료 날짜가 일치하지 않습니다.";

    public ScheduleNotEqualsDayException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}

