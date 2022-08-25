package ruby.api.exception.schedule;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class ScheduleNotFoundException extends BusinessException {

    public static final String MESSAGE = "해당 스케줄이 존재하지 않습니다.";

    public ScheduleNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
