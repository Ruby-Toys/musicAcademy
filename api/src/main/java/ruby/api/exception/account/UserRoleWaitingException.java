package ruby.api.exception.account;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class UserRoleWaitingException extends BusinessException {

    public static final String MESSAGE = "해당 계정으로 로그인 할 수 없습니다. 관리자에게 문의하세요.";

    public UserRoleWaitingException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
