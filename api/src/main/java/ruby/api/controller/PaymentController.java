package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ruby.api.request.payment.PaymentPost;
import ruby.api.request.payment.PaymentSearch;
import ruby.api.response.payment.PaymentsResponse;
import ruby.api.service.PaymentService;
import ruby.core.domain.Payment;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public void post(@RequestBody PaymentPost paymentPost) {
        paymentService.add(paymentPost);
    }

    @GetMapping
    public PaymentsResponse getList(@Valid PaymentSearch search) {
        Page<Payment> payments = paymentService.getList(search);
        return new PaymentsResponse(payments);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        paymentService.delete(id);
    }
}
