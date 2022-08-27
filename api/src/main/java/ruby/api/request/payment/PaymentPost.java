package ruby.api.request.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PaymentPost {

    private Long studentId;
    private Long amount;
}
