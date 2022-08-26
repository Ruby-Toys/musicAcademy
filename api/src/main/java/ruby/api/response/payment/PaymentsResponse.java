package ruby.api.response.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import ruby.core.domain.Payment;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PaymentsResponse {

    private List<PaymentResponse> contents;
    private int page;
    private long totalCount;
    private int pageSize;

    public PaymentsResponse(Page<Payment> paymentPage) {
        this.contents = paymentPage.getContent().stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
        this.page = paymentPage.getPageable().getPageNumber() + 1;
        this.totalCount = paymentPage.getTotalElements();
        this.pageSize = paymentPage.getPageable().getPageSize();
    }
}
