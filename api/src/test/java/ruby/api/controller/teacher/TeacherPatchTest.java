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
import ruby.api.request.teacher.TeacherPatch;
import ruby.api.valid.EmailPattern;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PhonePattern;
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.Course;
import ruby.core.repository.TeacherRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
public class TeacherPatchTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    TeacherRepository teacherRepository;

    @BeforeEach
    void before() {
        teacherRepository.deleteAll();

        Teacher teacher = Teacher.builder()
                .name("teacher")
                .course(Course.VIOLIN)
                .email("teacher@naver.com")
                .phoneNumber("01011112222")
                .build();
        teacherRepository.save(teacher);
    }


    @Test
    @DisplayName("잘못된 필드로 수강생 정보 수정")
    void edit_wrongField() throws Exception {
        // given
        Teacher teacher = teacherRepository.findAll().get(0);
        TeacherPatch teacherPatch = TeacherPatch.builder()
                .name("!@#")
                .email("testnaver.com")
                .phoneNumber("01023233423123")
                .build();

        // when
        mockMvc.perform(patch("/teachers/{id}", teacher.getId() + 999)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(teacherPatch))
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
    @DisplayName("존재하지 않는 수강생 정보 수정")
    void edit_noneTeacher() throws Exception {
        // given
        Teacher teacher = teacherRepository.findAll().get(0);
        TeacherPatch teacherPatch = TeacherPatch.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .build();

        // when
        mockMvc.perform(patch("/teachers/{id}", teacher.getId() + 999)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(teacherPatch))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        // then
        Teacher updatedTeacher = teacherRepository.findAll().get(0);
        assertThat(teacher.getId()).isEqualTo(updatedTeacher.getId());
        assertThat(teacher.getName()).isEqualTo(updatedTeacher.getName());
        assertThat(teacher.getEmail()).isEqualTo(updatedTeacher.getEmail());
        assertThat(teacher.getPhoneNumber()).isEqualTo(updatedTeacher.getPhoneNumber());
        assertThat(teacher.getCourse()).isEqualTo(updatedTeacher.getCourse());
    }

    @Test
    @DisplayName("수강생 정보 수정")
    void edit() throws Exception {
        // given
        Teacher teacher = teacherRepository.findAll().get(0);
        TeacherPatch teacherPatch = TeacherPatch.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .build();

        // when
        mockMvc.perform(patch("/teachers/{id}", teacher.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(teacherPatch))
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Teacher updatedTeacher = teacherRepository.findAll().get(0);
        assertThat(teacher.getId()).isEqualTo(updatedTeacher.getId());
        assertThat(updatedTeacher.getName()).isEqualTo(teacherPatch.getName());
        assertThat(updatedTeacher.getEmail()).isEqualTo(teacherPatch.getEmail());
        assertThat(updatedTeacher.getPhoneNumber()).isEqualTo(teacherPatch.getPhoneNumber());
        assertThat(updatedTeacher.getCourse()).isEqualTo(teacherPatch.getCourse());
    }
}
