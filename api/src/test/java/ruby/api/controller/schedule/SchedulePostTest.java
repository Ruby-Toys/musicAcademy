package ruby.api.controller.schedule;

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
import ruby.api.exception.schedule.CourseDiscordException;
import ruby.api.exception.schedule.ScheduleExistsTimeException;
import ruby.api.exception.schedule.ScheduleWrongDateException;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.exception.teacher.TeacherNotFoundException;
import ruby.api.request.schedule.SchedulePost;
import ruby.api.utils.DateUtils;
import ruby.api.valid.LocalDateTimePattern;
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
import java.time.format.DateTimeFormatter;

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
public class SchedulePostTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    ScheduleRepository scheduleRepository;

    @BeforeEach
    void before() {
        scheduleRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();

        Student student = Student.builder()
                .name("student")
                .course(Course.VIOLIN)
                .email("student@naver.com")
                .grade(Grade.BEGINNER)
                .phoneNumber("01011112222")
                .memo("악기 연주에 소질이 있음")
                .build();
        studentRepository.save(student);

        Teacher teacher = Teacher.builder()
                .name("teacher")
                .course(Course.VIOLIN)
                .email("teacher@naver.com")
                .phoneNumber("01011112222")
                .build();
        teacherRepository.save(teacher);
    }

    @Test
    @DisplayName("과목이 일치하지 않는 수강생과 선생님의 스케줄 등록")
    void post_DiscordCourse() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = Teacher.builder()
                .name("teacher1")
                .course(Course.PIANO)
                .email("teacher1@naver.com")
                .phoneNumber("01011112222")
                .build();
        teacherRepository.save(teacher);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateUtils.formatter();
        SchedulePost schedulePost = SchedulePost.builder()
                .start(now.format(formatter))
                .end(now.plusHours(1).format(formatter))
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .build();

        // when
        mockMvc.perform(post("/schedules")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePost))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(CourseDiscordException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 필드로 스케줄 등록")
    void post_wrongField() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);
        SchedulePost schedulePost = SchedulePost.builder()
                .start("2022-13-10 25:11:00")
                .end("2022-13-10 25:11:00")
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .build();

        // when
        mockMvc.perform(post("/schedules")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePost))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andExpect(jsonPath("$.validation.start").value(LocalDateTimePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.end").value(LocalDateTimePattern.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 수강생으로 스케줄 등록")
    void postSchedule_noneStudent() throws Exception {
        // given
        Teacher teacher = teacherRepository.findAll().get(0);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateUtils.formatter();
        SchedulePost schedulePost = SchedulePost.builder()
                .start(now.format(formatter))
                .end(now.plusHours(1).format(formatter))
                .studentId(9990L)
                .teacherId(teacher.getId())
                .build();

        // when
        mockMvc.perform(post("/schedules")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePost))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(StudentNotFoundException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 선생님으로 스케줄 등록")
    void postSchedule_noneTeacher() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateUtils.formatter();
        SchedulePost schedulePost = SchedulePost.builder()
                .start(now.format(formatter))
                .end(now.plusHours(1).format(formatter))
                .studentId(student.getId())
                .teacherId(999L)
                .build();

        // when
        mockMvc.perform(post("/schedules")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePost))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(TeacherNotFoundException.MESSAGE))
                .andDo(print());
    }


    @Test
    @DisplayName("스케줄 시작 시간이 종료 시간 보다 이전으로 스케줄 등록")
    void postSchedule_wrongDate() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateUtils.formatter();
        SchedulePost schedulePost = SchedulePost.builder()
                .start(now.plusHours(1).format(formatter))
                .end(now.format(formatter))
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .build();

        // when
        mockMvc.perform(post("/schedules")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePost))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ScheduleWrongDateException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("이미 스케줄이 잡힌 시간에 스케줄 등록")
    void postSchedule_existsTime() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);
        DateTimeFormatter formatter = DateUtils.formatter();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Schedule schedule = Schedule.builder()
                .student(student)
                .teacher(teacher)
                .start(start)
                .end(end)
                .state(ScheduleState.NOT_STARTED)
                .build();
        scheduleRepository.save(schedule);

        SchedulePost schedulePost = SchedulePost.builder()
                .start(start.plusMinutes(30).format(formatter))
                .end(end.plusHours(1).format(formatter))
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .build();

        // when
        mockMvc.perform(post("/schedules")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePost))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ScheduleExistsTimeException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("스케줄 등록")
    void postSchedule() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateUtils.formatter();
        SchedulePost schedulePost = SchedulePost.builder()
                .start(now.format(formatter))
                .end(now.plusHours(1).format(formatter))
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .build();

        // when
        mockMvc.perform(post("/schedules")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePost))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(schedulePost.getStudentId()))
                .andExpect(jsonPath("$.teacherId").value(schedulePost.getTeacherId()))
                .andExpect(jsonPath("$.start").value(schedulePost.getStart()))
                .andExpect(jsonPath("$.end").value(schedulePost.getEnd()))
                .andExpect(jsonPath("$.studentName").value(student.getName()))
                .andExpect(jsonPath("$.teacherName").value(teacher.getName()))
                .andDo(print());
    }
}
