package ruby.api.exception.account;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class NotPermissionException extends BusinessException {
    public static final String MESSAGE = "권한이 없습니다.";

    public NotPermissionException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
