package ruby.core.repository.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import ruby.core.domain.Payment;
import ruby.core.repository.custom.PaymentRepositoryCustom;

import java.util.List;
import java.util.Optional;

import static ruby.core.domain.QPayment.payment;
import static ruby.core.domain.QStudent.student;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Payment> findByNameContains(String word, Pageable pageable) {
        List<Payment> payments = queryFactory.selectFrom(payment)
                .leftJoin(payment.student, student).fetchJoin()
                .where(searchCondition(word))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(payment.id.desc())
                .fetch();

        int size = queryFactory.selectFrom(payment)
                .where(searchCondition(word))
                .fetch()
                .size();

        return PageableExecutionUtils.getPage(payments, pageable, () -> size);
    }

    @Override
    public Optional<Payment> findByIdWithStudent(Long id) {
        Payment findPayment = queryFactory.selectFrom(payment)
                .leftJoin(payment.student, student).fetchJoin()
                .where(payment.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(findPayment);
    }

    private Predicate searchCondition(String word) {
        return word == null ? null : student.name.contains(word);
    }
}
