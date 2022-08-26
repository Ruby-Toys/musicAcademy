package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ruby.api.exception.PeriodException;
import ruby.api.request.payment.PaymentSearch;
import ruby.api.response.payment.PaymentsResponse;
import ruby.api.service.PaymentService;
import ruby.api.utils.DateUtils;
import ruby.core.domain.Payment;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public PaymentsResponse getList(@Valid PaymentSearch search) {
        Page<Payment> payments = paymentService.getList(search);
        return new PaymentsResponse(payments);
    }
}
