package ruby.api.controller.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.controller.ExceptionController;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.request.payment.PaymentPost;
import ruby.api.request.student.StudentPost;
import ruby.api.valid.*;
import ruby.core.domain.Payment;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.PaymentRepository;
import ruby.core.repository.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
public class PaymentPostTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @BeforeEach
    void before() {
        paymentRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("존재하지 않는 수강생으로 결재내역 등록")
    void postPayment_noneStudent() throws Exception {
        // given
        Student student = Student.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01011112222")
                .course(Course.VIOLIN)
                .grade(Grade.BEGINNER)
                .build();
        studentRepository.save(student);

        PaymentPost paymentPost = PaymentPost.builder()
                .studentId(student.getId() + 999)
                .amount(160000L)
                .build();

        // when
        mockMvc.perform(post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(paymentPost))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(StudentNotFoundException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("결제 내역 등록")
    void postPayment() throws Exception {
        // given
        Student student = Student.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01011112222")
                .course(Course.VIOLIN)
                .grade(Grade.BEGINNER)
                .build();
        studentRepository.save(student);

        PaymentPost paymentPost = PaymentPost.builder()
                .studentId(student.getId())
                .amount(160000L)
                .build();

        // when
        mockMvc.perform(post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(paymentPost))
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Payment payment = paymentRepository.findAll().get(0);
        assertThat(payment.getStudent().getId()).isEqualTo(paymentPost.getStudentId());
        assertThat(payment.getAmount()).isEqualTo(paymentPost.getAmount());
    }
}
