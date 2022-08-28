package ruby.api.exception.payment;

import org.springframework.http.HttpStatus;
import ruby.api.exception.BusinessException;

public class PaymentNotFoundException extends BusinessException {

    public static final String MESSAGE = "해당 결제 내역이 존재하지 않습니다.";

    public PaymentNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
