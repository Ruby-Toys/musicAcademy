package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.payment.PaymentNotFoundException;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.request.payment.PaymentPost;
import ruby.api.request.payment.PaymentSearch;
import ruby.api.request.student.StudentSearch;
import ruby.api.utils.DateUtils;
import ruby.core.domain.Payment;
import ruby.core.domain.Student;
import ruby.core.repository.PaymentRepository;
import ruby.core.repository.StudentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.max;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private static final int REMAINDER_COUNT = 4;

    public void add(PaymentPost paymentPost) {
        Student student = studentRepository.findById(paymentPost.getStudentId())
                .orElseThrow(StudentNotFoundException::new);

        Payment payment = Payment.builder()
                .student(student)
                .amount(paymentPost.getAmount())
                .build();
        paymentRepository.save(payment);
        student.addPayment();
    }

    public Page<Payment> getList(PaymentSearch search) {
        Pageable pageable = PageRequest.of(max(0, search.getPage() - 1), PaymentSearch.PAGE_SIZE);
        return paymentRepository.findByNameContains(search.getWord(), pageable);
    }

    public void delete(long id) {
        Payment payment = paymentRepository.findByIdWithStudent(id)
                .orElseThrow(PaymentNotFoundException::new);

        Student student = payment.getStudent();
        if (student.getRemainderCnt() < REMAINDER_COUNT) {
            student.setRemainderCnt(0);
        } else {
            payment.getStudent().cancelPayment();
        }

        paymentRepository.delete(payment);
    }
}
