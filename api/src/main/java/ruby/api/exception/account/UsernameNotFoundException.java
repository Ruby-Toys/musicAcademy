package ruby.api.exception.account;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class UsernameNotFoundException extends BusinessException {

    public static final String MESSAGE = "해당 아이디로 등록된 계정이 존재하지 않습니다.";

    public UsernameNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
