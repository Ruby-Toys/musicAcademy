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
import ruby.api.exception.teacher.TeacherNotFoundException;
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.Course;
import ruby.core.repository.TeacherRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
public class TeacherDeleteTest {

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
    @DisplayName("존재하지 않는 선생님 정보 삭제")
    void deleteTeacher_noneTeacher() throws Exception {
        // given
        Teacher teacher = Teacher.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .build();
        teacherRepository.save(teacher);

        // when
        mockMvc.perform(delete("/teachers/{id}", teacher.getId() + 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.NOT_FOUND_MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("선생님 정보 삭제")
    void deleteTeacher() throws Exception {
        // given
        Teacher teacher = Teacher.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .build();
        teacherRepository.save(teacher);

        // when
        mockMvc.perform(delete("/teachers/{id}", teacher.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        assertThat(teacherRepository.existsById(teacher.getId())).isFalse();
    }
}
