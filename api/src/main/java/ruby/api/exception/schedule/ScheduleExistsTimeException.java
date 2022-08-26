package ruby.api.exception.schedule;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class ScheduleExistsTimeException extends BusinessException {

    public static final String MESSAGE = "해당 시간에 스케줄이 이미 존재합니다.";

    public ScheduleExistsTimeException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}

