package ruby.api.response.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import ruby.core.domain.Payment;

import java.time.LocalDateTime;

@Getter
public class PaymentResponse {

    private Long id;
    private String studentName;
    private String phoneNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime paymentDate;
    private Long amount;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.studentName = payment.getStudent().getName();
        this.phoneNumber = payment.getStudent().getPhoneNumber();
        this.paymentDate = payment.getCreateAt();
        this.amount = payment.getAmount();
    }
}
