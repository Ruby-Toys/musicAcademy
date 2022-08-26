package ruby.api.response.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ruby.core.domain.Payment;

import java.time.LocalDateTime;

@Getter @Setter
public class PaymentResponse {

    private Long id;
    private String studentName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime paymentDate;
    private Long amount;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.studentName = payment.getStudent().getName();
        this.paymentDate = payment.getCreateAt();
        this.amount = payment.getAmount();
    }
}
