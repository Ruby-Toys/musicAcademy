package ruby.api.controller.teacher;

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
import ruby.api.request.teacher.TeacherPost;
import ruby.api.valid.EmailPattern;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PhonePattern;
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.Course;
import ruby.core.repository.TeacherRepository;

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
public class TeacherPostTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    TeacherRepository teacherRepository;

    @BeforeEach
    void before() {
        teacherRepository.deleteAll();
    }


    @Test
    @DisplayName("잘못된 필드로 선생님 정보 등록")
    void post_wrongField() throws Exception {
        // given
        TeacherPost teacher = TeacherPost.builder()
                .name("!@#")
                .email("testnaver.com")
                .phoneNumber("01023233423123")
                .build();

        // when
        mockMvc.perform(post("/teachers")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(teacher))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andExpect(jsonPath("$.validation.phoneNumber").value(PhonePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.name").value(NamePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.email").value(EmailPattern.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("선생님 정보 수정")
    void postTeacher() throws Exception {
        // given
        TeacherPost teacher = TeacherPost.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .build();

        // when
        mockMvc.perform(post("/teachers")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(teacher))
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Teacher addTeacher = teacherRepository.findAll().get(0);
        assertThat(teacher.getName()).isEqualTo(addTeacher.getName());
        assertThat(teacher.getEmail()).isEqualTo(addTeacher.getEmail());
        assertThat(teacher.getPhoneNumber()).isEqualTo(addTeacher.getPhoneNumber());
        assertThat(teacher.getCourse()).isEqualTo(addTeacher.getCourse());
    }
}
