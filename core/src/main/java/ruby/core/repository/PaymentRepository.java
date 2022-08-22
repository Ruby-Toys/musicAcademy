package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
