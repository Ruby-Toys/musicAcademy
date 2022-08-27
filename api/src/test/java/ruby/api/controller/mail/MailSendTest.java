package ruby.api.controller.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
class MailSendTest {

    @Autowired
    MockMvc mockMvc;
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
    }

    @Test
    @DisplayName("다음 날 스케줄이 없는 경우에 이메일 전송 요청")
    void sendMail_noneSchedule() throws Exception {
        Student student = Student.builder()
                .name("student")
                .course(Course.VIOLIN)
                .email("rubykim0723@gmail.com")
                .grade(Grade.BEGINNER)
                .build();
        studentRepository.save(student);

        Teacher teacher = Teacher.builder()
                .name("teacher")
                .course(Course.VIOLIN)
                .email("teacher@naver.com")
                .build();
        teacherRepository.save(teacher);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(),
                now.getDayOfMonth(), 20, 0).plusDays(2);
        Schedule schedule = Schedule.builder()
                .start(start)
                .end(start.plusHours(1))
                .state(ScheduleState.NOT_STARTED)
                .student(student)
                .teacher(teacher)
                .build();
        scheduleRepository.save(schedule);

        mockMvc.perform(get("/mail"))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("이메일 전송 요청")
    void sendMail() throws Exception {
        Student student = Student.builder()
                .name("student")
                .course(Course.VIOLIN)
                .email("rubykim0723@gmail.com")
                .grade(Grade.BEGINNER)
                .build();
        studentRepository.save(student);

        Teacher teacher = Teacher.builder()
                .name("teacher")
                .course(Course.VIOLIN)
                .email("teacher@naver.com")
                .build();
        teacherRepository.save(teacher);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(),
                now.getDayOfMonth(), 20, 0).plusDays(1);
        Schedule schedule = Schedule.builder()
                .start(start)
                .end(start.plusHours(1))
                .state(ScheduleState.NOT_STARTED)
                .student(student)
                .teacher(teacher)
                .build();
        scheduleRepository.save(schedule);

        mockMvc.perform(get("/mail"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}