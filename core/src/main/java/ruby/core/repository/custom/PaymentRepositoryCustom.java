package ruby.core.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ruby.core.domain.Payment;

public interface PaymentRepositoryCustom {
    Page<Payment> findByNameContains(String word, Pageable pageable);
}
