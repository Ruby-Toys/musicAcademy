package ruby.api.controller.student;

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
import ruby.api.request.student.StudentPost;
import ruby.api.valid.*;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
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
public class StudentPostTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void before() {
        studentRepository.deleteAll();
    }


    @Test
    @DisplayName("잘못된 필드로 수강생 정보 등록")
    void post_wrongField() throws Exception {
        // given
        StudentPost student = StudentPost.builder()
                .name("!@#")
                .course("QWE")
                .grade("QWEQWE")
                .email("testnaver.com")
                .phoneNumber("01023233423123")
                .memo("고급 단계로 변경")
                .build();

        // when
        mockMvc.perform(post("/students")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(student))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andExpect(jsonPath("$.validation.phoneNumber").value(PhonePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.name").value(NamePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.email").value(EmailPattern.MESSAGE))
                .andExpect(jsonPath("$.validation.course").value(CoursePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.grade").value(GradePattern.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("수강생 정보 수정")
    void postStudent() throws Exception {
        // given
        StudentPost student = StudentPost.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE.name())
                .grade(Grade.BEGINNER.name())
                .memo("수강생 신규 등록")
                .build();

        // when
        mockMvc.perform(post("/students")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(student))
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Student addStudent = studentRepository.findAll().get(0);
        assertThat(student.getName()).isEqualTo(addStudent.getName());
        assertThat(student.getEmail()).isEqualTo(addStudent.getEmail());
        assertThat(student.getPhoneNumber()).isEqualTo(addStudent.getPhoneNumber());
        assertThat(student.getCourse()).isEqualTo(addStudent.getCourse().name());
        assertThat(student.getGrade()).isEqualTo(addStudent.getGrade().name());
        assertThat(student.getMemo()).isEqualTo(addStudent.getMemo());
    }
}
