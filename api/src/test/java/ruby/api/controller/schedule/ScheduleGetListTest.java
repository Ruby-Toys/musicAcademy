package ruby.api.controller.schedule;

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
import ruby.api.valid.CoursePattern;
import ruby.api.valid.DatePattern;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ScheduleGetListTest {

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

        Student student = Student.builder()
                .name("student")
                .course(Course.VIOLIN)
                .email("student@naver.com")
                .grade(Grade.BEGINNER)
                .phoneNumber("010-1111-2222")
                .memo("악기 연주에 소질이 있음")
                .build();
        studentRepository.save(student);

        Teacher teacher = Teacher.builder()
                .name("teacher")
                .course(Course.VIOLIN)
                .email("teacher@naver.com")
                .phoneNumber("010-1111-2222")
                .build();
        teacherRepository.save(teacher);
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = IntStream.range(0, 8)
                .mapToObj(idx -> Schedule.builder()
                        .appointmentTime(
                                LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                                        10 + idx, 0))
                        .state(ScheduleState.NOT_STARTED)
                        .student(student)
                        .teacher(teacher)
                        .build()
                )
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
        schedules = IntStream.range(0, 8)
                .mapToObj(idx -> Schedule.builder()
                        .appointmentTime(
                                LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth() - 7,
                                        10 + idx, 0))
                        .state(ScheduleState.NOT_STARTED)
                        .student(student)
                        .teacher(teacher)
                        .build()
                )
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
    }

    @Test
    @DisplayName("잘못된 날짜 형식으로 스케쥴 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList_wrongAppointmentTime() throws Exception {
        mockMvc.perform(get("/schedules")
                        .param("course", Course.VIOLIN.name())
                        .param("appointmentTime", "2022 08 30")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andExpect(jsonPath("$.validation.appointmentTime").value(DatePattern.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 과목으로 스케쥴 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList_wrongCourse() throws Exception {
        mockMvc.perform(get("/schedules")
                        .param("course", "DRUM")
                        .param("appointmentTime", "2022-08-30")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andExpect(jsonPath("$.validation.course").value(CoursePattern.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("수강생이 없는 과목으로 스케쥴 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList_noneCourse() throws Exception {
        mockMvc.perform(get("/schedules")
                        .param("course", Course.FLUTE.name())
                        .param("appointmentTime", "2022-08-30")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.length()").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("스케쥴이 없는 기간으로 스케쥴 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList_noneAppointmentTime() throws Exception {
        mockMvc.perform(get("/schedules")
                        .param("course", Course.VIOLIN.name())
                        .param("appointmentTime", "2022-08-30")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.length()").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("스케쥴 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList() throws Exception {
        mockMvc.perform(get("/schedules")
                        .param("course", Course.VIOLIN.name())
                        .param("appointmentTime", "2022-08-23")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.length()").value(8))
                .andDo(print());
    }
}