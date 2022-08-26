package ruby.api.exception.schedule;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class ScheduleWrongDateException extends BusinessException {

    public static final String MESSAGE = "시작 날짜는 종료 날짜 이전이어야 합니다.";

    public ScheduleWrongDateException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}

