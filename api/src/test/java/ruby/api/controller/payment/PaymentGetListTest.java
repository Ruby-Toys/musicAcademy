package ruby.api.controller.payment;

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
import ruby.api.request.payment.PaymentSearch;
import ruby.api.utils.DateUtils;
import ruby.core.domain.Payment;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.PaymentRepository;
import ruby.core.repository.StudentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
class PaymentGetListTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @BeforeEach
    void before() {
        paymentRepository.deleteAll();
        studentRepository.deleteAll();

        List<Student> students = IntStream.range(0, 10)
                .mapToObj(idx -> Student.builder()
                        .name("student" + ++idx)
                        .course(idx % 2 == 0 ? Course.VIOLIN : Course.PIANO)
                        .email("student" + idx + "@naver.com")
                        .grade(idx % 2 == 0 ? Grade.BEGINNER : Grade.INTERMEDIATE)
                        .phoneNumber("01011112222")
                        .memo("악기 연주에 소질이 있음")
                        .build()
                )
                .collect(Collectors.toList());
        studentRepository.saveAll(students);

        for (Student student : students) {
            IntStream.range(0, 5)
                    .forEach(idx -> {
                        Payment payment = Payment.builder()
                                .student(student)
                                .amount(student.getGrade().getAmount())
                                .build();
                        paymentRepository.save(payment);
                        payment.setCreateAt(LocalDateTime.now().minusMonths(idx));
                        paymentRepository.save(payment);
                    });
        }
    }

    @Test
    @DisplayName("잘못된 형식의 페이지 번호로 결제 내역 조회")
    void getList_wrongPage() throws Exception {
        // when
        mockMvc.perform(get("/payments")
                        .param("page", "asdasd")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("페이지 번호 없이 결제 내역 조회")
    void getList_nonePage() throws Exception {
        // given
        int totalCount = paymentRepository.findAll().size();

        // when
        mockMvc.perform(get("/payments")
                        .param("word", "student")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.totalCount").value(totalCount))
                .andExpect(jsonPath("$.pageSize").value(PaymentSearch.PAGE_SIZE))
                .andExpect(jsonPath("$.contents.length()").value(PaymentSearch.PAGE_SIZE))
                .andDo(print());
    }

    @Test
    @DisplayName("검색어 없이 결제 내역 조회")
    void getList_emptyWord() throws Exception {
        // given
        int totalCount = paymentRepository.findAll().size();

        // when
        mockMvc.perform(get("/payments")
                        .param("page", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.totalCount").value(totalCount))
                .andExpect(jsonPath("$.pageSize").value(PaymentSearch.PAGE_SIZE))
                .andExpect(jsonPath("$.contents.length()").value(PaymentSearch.PAGE_SIZE))
                .andDo(print());
    }

    @Test
    @DisplayName("결제 내역 조회")
    void getList() throws Exception {
        // given
        String word = "dent";
        int totalCount = paymentRepository.findAll().size();

        // when
        mockMvc.perform(get("/payments")
                    .param("word", word)
                    .param("page", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.totalCount").value(totalCount))
                .andExpect(jsonPath("$.pageSize").value(PaymentSearch.PAGE_SIZE))
                .andExpect(jsonPath("$.contents.length()").value(PaymentSearch.PAGE_SIZE))
                .andDo(print());
    }
}