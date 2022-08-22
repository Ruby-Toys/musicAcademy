package ruby.api.exception.account;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class UserBadCredentialsException extends BusinessException {

    public static final String MESSAGE = "아이디 또는 비밀번호가 일치하지 않습니다.";

    public UserBadCredentialsException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
