package ruby.api.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.request.payment.PaymentPost;
import ruby.core.domain.Payment;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.PaymentRepository;
import ruby.core.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "test", roles = "MANAGER")
@AutoConfigureRestDocs(uriHost = "musicAcademy")
@ExtendWith(RestDocumentationExtension.class)
@Transactional
public class PaymentDocTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void before() {
        paymentRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("?????? ?????? ??????")
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
                .andDo(document("payment-post",
                        requestFields(
                                fieldWithPath("studentId").description("????????? ??????"),
                                fieldWithPath("amount").description("????????????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void getList() throws Exception {
        // given
        String word = "dent";
        List<Student> students = IntStream.range(0, 10)
                .mapToObj(idx -> Student.builder()
                        .name("student" + ++idx)
                        .course(idx % 2 == 0 ? Course.VIOLIN : Course.PIANO)
                        .email("student" + idx + "@naver.com")
                        .grade(idx % 2 == 0 ? Grade.BEGINNER : Grade.INTERMEDIATE)
                        .phoneNumber("01011112222")
                        .memo("?????? ????????? ????????? ??????")
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

        // when
        mockMvc.perform(get("/payments")
                        .param("word", word)
                        .param("page", "1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("payment-getList",
                        requestParameters(
                                parameterWithName("word").description("?????????").optional(),
                                parameterWithName("page").description("????????? ??????").optional()
                        ),
                        responseFields(
                                fieldWithPath("page").description("?????? ?????????"),
                                fieldWithPath("pageSize").description("????????? ??? ?????? ??????"),
                                fieldWithPath("totalCount").description("?????? ?????? ??? ??????"),

                                fieldWithPath("contents[].id").description("???????????? ??????"),
                                fieldWithPath("contents[].studentName").description("????????? ??????"),
                                fieldWithPath("contents[].phoneNumber").description("?????????"),
                                fieldWithPath("contents[].paymentDate").description("????????????"),
                                fieldWithPath("contents[].amount").description("????????????")
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ??????")
    void deletePayment() throws Exception {
        // given
        Student student = Student.builder()
                .name("student")
                .course(Course.VIOLIN)
                .email("student@naver.com")
                .grade(Grade.BEGINNER)
                .phoneNumber("01011112222")
                .memo("?????? ????????? ????????? ??????")
                .build();
        studentRepository.save(student);
        Payment payment = Payment.builder()
                .student(student)
                .amount(student.getGrade().getAmount())
                .build();
        paymentRepository.save(payment);

        // when
        mockMvc.perform(delete("/payments/{id}", payment.getId()))
                .andExpect(status().isOk())
                .andDo(document("payment-delete",
                        pathParameters(
                                parameterWithName("id").description("??????")
                        )
                ));
    }
}
