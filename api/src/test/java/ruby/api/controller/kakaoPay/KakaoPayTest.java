package ruby.api.controller.kakaoPay;

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
import ruby.api.exception.student.StudentNotFoundException;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.PaymentRepository;
import ruby.core.repository.StudentRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
class KakaoPayTest {

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
    }
    @Test
    @DisplayName("존재하지 않는 수강생으로 카카오 페이 결제 요청 호출")
    void ready_noneStudent() throws Exception {
        mockMvc.perform(get("/kakaoPay/ready")
                        .param("studentId","99999")
                        .param("amount", "170000")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(StudentNotFoundException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("카카오 페이 결제 요청 호출")
    void ready() throws Exception {
        // given
        Student student = Student.builder()
                .name("student")
                .course(Course.VIOLIN)
                .email("student@naver.com")
                .grade(Grade.BEGINNER)
                .phoneNumber("01011112222")
                .memo("악기 연주에 소질이 있음")
                .build();
        studentRepository.save(student);

        mockMvc.perform(get("/kakaoPay/ready")
                    .param("studentId", String.valueOf(student.getId()))
                    .param("amount", "170000")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tid").exists())
                .andExpect(jsonPath("$.next_redirect_pc_url").exists())
                .andExpect(jsonPath("$.created_at").exists())
                .andDo(print());
    }



    @Test
    @DisplayName("카카오 페이 결제 완료 호출")
    void success() throws Exception {
        mockMvc.perform(get("/kakaoPay/success")
                        .param("pgToken", "asd1231GWEsdaHF")
                )
                .andDo(print());
    }
    // 827022744220
    // a160a5d3-9a4f-40a5-ac82-2889e43ab6db
}