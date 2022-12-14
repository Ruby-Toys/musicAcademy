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
    @DisplayName("????????? ?????? ?????? ?????? ??????")
    void ready() throws Exception {
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

        mockMvc.perform(get("/kakaoPay/ready")
                    .param("studentId", String.valueOf(student.getId()))
                    .param("amount", "170000")
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("kakaoPay-ready",
                        requestParameters(
                                parameterWithName("studentId").description("????????? ID"),
                                parameterWithName("amount").description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("tid").description("?????? ?????? ??????"),
                                fieldWithPath("next_redirect_pc_url")
                                        .description("?????????????????? ?????? ?????? ?????????(TMS)??? ????????? ?????? ????????? ?????? ?????? ?????? Redirect URL"),
                                fieldWithPath("created_at").description("?????? ?????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ??????")
    void success() throws Exception {
        mockMvc.perform(get("/kakaoPay/success")
                        .param("pgToken", "asd1231GWEsdaHF")
                )
                .andDo(document("kakaoPay-success",
                        requestParameters(
                                parameterWithName("pgToken").description("???????????? ????????? ???????????? ??????")
                        )
                ));
    }
}