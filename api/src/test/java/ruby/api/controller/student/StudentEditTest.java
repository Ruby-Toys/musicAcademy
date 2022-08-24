package ruby.api.controller.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.controller.ExceptionController;
import ruby.api.request.student.StudentSearch;
import ruby.api.request.student.StudentUpdate;
import ruby.api.valid.EmailPattern;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PhonePattern;
import ruby.core.domain.Schedule;
import ruby.core.domain.Student;
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.domain.enums.ScheduleState;
import ruby.core.repository.ScheduleRepository;
import ruby.core.repository.StudentRepository;
import ruby.core.repository.TeacherRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
public class StudentEditTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void before() {
        studentRepository.deleteAll();

        Student student = Student.builder()
                .name("student")
                .course(Course.VIOLIN)
                .email("student@naver.com")
                .grade(Grade.BEGINNER)
                .phoneNumber("010-1111-2222")
                .memo("악기 연주에 소질이 있음")
                .build();
        studentRepository.save(student);
    }


    @Test
    @DisplayName("잘못된 필드로 수강생 정보 수정")
    void edit_wrongName() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        StudentUpdate studentUpdate = StudentUpdate.builder()
                .name("!@#")
                .email("testnaver.com")
                .phoneNumber("010-2323-3423123")
                .memo("고급 단계로 변경")
                .build();

        // when
        mockMvc.perform(patch("/students/{id}", student.getId() + 999)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(studentUpdate))
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
    void edit_noneStudent() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        StudentUpdate studentUpdate = StudentUpdate.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("010-2323-3423")
                .course(Course.FLUTE)
                .grade(Grade.ADVANCED)
                .memo("고급 단계로 변경")
                .build();

        // when
        mockMvc.perform(patch("/students/{id}", student.getId() + 999)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(studentUpdate))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        // then
        Student updatedStudent = studentRepository.findAll().get(0);
        assertThat(student.getId()).isEqualTo(updatedStudent.getId());
        assertThat(student.getName()).isEqualTo(updatedStudent.getName());
        assertThat(student.getEmail()).isEqualTo(updatedStudent.getEmail());
        assertThat(student.getPhoneNumber()).isEqualTo(updatedStudent.getPhoneNumber());
        assertThat(student.getCourse()).isEqualTo(updatedStudent.getCourse());
        assertThat(student.getGrade()).isEqualTo(updatedStudent.getGrade());
        assertThat(student.getMemo()).isEqualTo(updatedStudent.getMemo());
    }

    @Test
    @DisplayName("수강생 정보 수정")
    void edit() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        StudentUpdate studentUpdate = StudentUpdate.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("010-2323-3423")
                .course(Course.FLUTE)
                .grade(Grade.ADVANCED)
                .memo("고급 단계로 변경")
                .build();

        // when
        mockMvc.perform(patch("/students/{id}", student.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(studentUpdate))
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Student updatedStudent = studentRepository.findAll().get(0);
        assertThat(student.getId()).isEqualTo(updatedStudent.getId());
        assertThat(updatedStudent.getName()).isEqualTo(studentUpdate.getName());
        assertThat(updatedStudent.getEmail()).isEqualTo(studentUpdate.getEmail());
        assertThat(updatedStudent.getPhoneNumber()).isEqualTo(studentUpdate.getPhoneNumber());
        assertThat(updatedStudent.getCourse()).isEqualTo(studentUpdate.getCourse());
        assertThat(updatedStudent.getGrade()).isEqualTo(studentUpdate.getGrade());
        assertThat(updatedStudent.getMemo()).isEqualTo(studentUpdate.getMemo());
    }
}
