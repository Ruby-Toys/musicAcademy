package ruby.api.controller.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
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
import ruby.api.request.student.StudentAdd;
import ruby.api.valid.EmailPattern;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PhonePattern;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.StudentRepository;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
public class StudentDeleteTest {

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
    @DisplayName("존재하지 않는 수강생 정보 삭제")
    void deleteStudent_noneStudent() throws Exception {
        // given
        Student student = Student.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .grade(Grade.BEGINNER)
                .memo("수강생 신규 등록")
                .build();
        studentRepository.save(student);

        // when
        mockMvc.perform(delete("/students/{id}", student.getId() + 999))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(StudentNotFoundException.MESSAGE))
                .andDo(print());

        // then
        Student findStudent = studentRepository.findById(student.getId()).orElse(null);
        assertThat(findStudent).isNotNull();
        assertThat(findStudent.getId()).isEqualTo(student.getId());
    }

    @Test
    @DisplayName("수강생 정보 삭제")
    void deleteStudent() throws Exception {
        // given
        Student student = Student.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .grade(Grade.BEGINNER)
                .memo("수강생 신규 등록")
                .build();
        studentRepository.save(student);

        // when
        mockMvc.perform(delete("/students/{id}", student.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Student findStudent = studentRepository.findById(student.getId()).orElse(null);
        assertThat(findStudent).isNull();
    }
}
