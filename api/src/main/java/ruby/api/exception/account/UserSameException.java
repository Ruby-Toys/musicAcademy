package ruby.api.exception.account;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class UserSameException extends BusinessException {

    public static final String MESSAGE = "해당 아이디로 등록된 계정이 이미 존재합니다.";

    public UserSameException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
