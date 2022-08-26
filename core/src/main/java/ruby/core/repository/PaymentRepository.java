package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Payment;
import ruby.core.repository.custom.PaymentRepositoryCustom;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {

}
