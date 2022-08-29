package ruby.api.docs;

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
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.PaymentRepository;
import ruby.core.repository.StudentRepository;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "test", roles = "MANAGER")
@AutoConfigureRestDocs(uriHost = "musicAcademy")
@ExtendWith(RestDocumentationExtension.class)
@Transactional
class KakaoPayDocTest {

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
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("kakaoPay-ready",
                        requestParameters(
                                parameterWithName("studentId").description("수강생 ID"),
                                parameterWithName("amount").description("결제금액")
                        ),
                        responseFields(
                                fieldWithPath("tid").description("결제 고유 번호"),
                                fieldWithPath("next_redirect_pc_url")
                                        .description("카카오톡으로 결제 요청 메시지(TMS)를 보내기 위한 사용자 정보 입력 화면 Redirect URL"),
                                fieldWithPath("created_at").description("결제 준비 요청 시간")
                        )
                ));
    }

    @Test
    @DisplayName("카카오 페이 결제 완료 호출")
    void success() throws Exception {
        mockMvc.perform(get("/kakaoPay/success")
                        .param("pgToken", "asd1231GWEsdaHF")
                )
                .andDo(document("kakaoPay-success",
                        requestParameters(
                                parameterWithName("pgToken").description("결제승인 요청을 인증하는 토큰")
                        )
                ));
    }
}