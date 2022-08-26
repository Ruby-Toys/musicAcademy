package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.request.payment.PaymentSearch;
import ruby.api.request.student.StudentSearch;
import ruby.api.utils.DateUtils;
import ruby.core.domain.Payment;
import ruby.core.repository.PaymentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.max;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Page<Payment> getList(PaymentSearch search) {
        Pageable pageable = PageRequest.of(max(0, search.getPage() - 1), PaymentSearch.PAGE_SIZE);
        return paymentRepository.findByNameContains(search.getWord(), pageable);
    }
}
