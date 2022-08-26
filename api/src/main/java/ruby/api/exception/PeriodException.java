package ruby.api.exception;

import org.springframework.http.HttpStatus;

public class PeriodException extends BusinessException {

    public static final String MESSAGE = "시작 날짜는 종료 날짜 이전이어야 합니다.";

    public PeriodException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}

