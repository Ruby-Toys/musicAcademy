package ruby.api.request.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentPost {

    private Long studentId;
    private Long amount;

    @Builder
    public PaymentPost(Long studentId, Long amount) {
        this.studentId = studentId;
        this.amount = amount;
    }
}
