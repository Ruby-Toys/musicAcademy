package ruby.core.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ruby.core.domain.Payment;

import java.util.Optional;

public interface PaymentRepositoryCustom {
    Page<Payment> findByNameContains(String word, Pageable pageable);

    Optional<Payment> findByIdWithStudent(Long id);
}
