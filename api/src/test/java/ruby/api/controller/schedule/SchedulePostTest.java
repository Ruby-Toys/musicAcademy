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
import ruby.api.exception.schedule.ScheduleNotEqualsDayException;
import ruby.api.exception.PeriodException;
import ruby.api.exception.student.StudentNoneRemainderCountException;
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

import java.time.LocalDate;
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
                .memo("?????? ????????? ????????? ??????")
                .remainderCnt(4)
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
    @DisplayName("????????? ???????????? ?????? ???????????? ???????????? ????????? ??????")
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
        LocalDateTime now = LocalDate.now().atTime(20, 0);
        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
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
    @DisplayName("????????? ????????? ????????? ??????")
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
    @DisplayName("???????????? ?????? ??????????????? ????????? ??????")
    void postSchedule_noneStudent() throws Exception {
        // given
        Teacher teacher = teacherRepository.findAll().get(0);
        LocalDateTime now = LocalDate.now().atTime(20, 0);
        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
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
    @DisplayName("???????????? ?????? ??????????????? ????????? ??????")
    void postSchedule_noneTeacher() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        LocalDateTime now = LocalDate.now().atTime(20, 0);
        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
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
    @DisplayName("????????? ?????? ????????? ?????? ?????? ?????? ???????????? ????????? ??????")
    void postSchedule_wrongDate() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);
        LocalDateTime now = LocalDate.now().atTime(20, 0);
        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
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
                .andExpect(jsonPath("$.message").value(PeriodException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ????????? ????????? ??????")
    void postSchedule_existsTime() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);
        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        LocalDateTime start = LocalDate.now().atTime(20, 0);
        LocalDateTime end = LocalDate.now().atTime(20, 0).plusHours(1);
        Schedule schedule = Schedule.builder()
                .student(student)
                .teacher(teacher)
                .start(start)
                .end(end)
                .state(ScheduleState.NOT_STARTED)
                .build();
        scheduleRepository.save(schedule);

        SchedulePost schedulePost = SchedulePost.builder()
                .start(start.format(formatter))
                .end(end.format(formatter))
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
    @DisplayName("?????? ????????? ?????? ????????? ??? ?????? ?????? ????????? ??????")
    void postSchedule_notEqualsDay() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);
        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        LocalDateTime start = LocalDate.now().atTime(20, 0);
        LocalDateTime end = LocalDate.now().atTime(20, 0).plusDays(1);

        SchedulePost schedulePost = SchedulePost.builder()
                .start(start.format(formatter))
                .end(end.format(formatter))
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
                .andExpect(jsonPath("$.message").value(ScheduleNotEqualsDayException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ????????? 0??? ??? ??????")
    void postSchedule_noneRemainderCnt() throws Exception {
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

        Teacher teacher = Teacher.builder()
                .name("teacher")
                .course(Course.VIOLIN)
                .email("teacher@naver.com")
                .phoneNumber("01011112222")
                .build();
        teacherRepository.save(teacher);

        LocalDateTime now = LocalDate.now().atTime(20, 0);
        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
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
                .andExpect(jsonPath("$.message").value(StudentNoneRemainderCountException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ??????")
    void postSchedule() throws Exception {
        // given
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);
        LocalDateTime now = LocalDate.now().atTime(20, 0);
        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
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
