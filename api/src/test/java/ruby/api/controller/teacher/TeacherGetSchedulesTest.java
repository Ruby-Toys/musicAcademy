package ruby.api.controller.teacher;

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
public class TeacherGetSchedulesTest {

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
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = IntStream.range(0, 4)
                .mapToObj(idx -> Schedule.builder()
                        .appointmentTime(
                                LocalDateTime.of(now.getYear(), now.getMonth(), (idx + 1) * 6,
                                        10 + idx, 0))
                        .state(idx < 2 ? ScheduleState.COMPLETED : ScheduleState.NOT_STARTED)
                        .student(student)
                        .teacher(teacher)
                        .build()
                )
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
        schedules = IntStream.range(0, 4)
                .mapToObj(idx -> Schedule.builder()
                        .appointmentTime(
                                LocalDateTime.of(now.getYear(), now.getMonthValue() - 1, (idx + 1) * 6,
                                        10 + idx, 0))
                        .state(ScheduleState.COMPLETED)
                        .student(student)
                        .teacher(teacher)
                        .build()
                )
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
    }

    @Test
    @DisplayName("존재하지 않는 선생님 스케쥴 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getSchedules_noneTeacher() throws Exception {
        // given
        Teacher teacher = teacherRepository.findAll().get(0);

        // when
        mockMvc.perform(get("/teachers/{id}/schedules", teacher.getId() + 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.length()").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("선생님의 스케쥴 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getSchedules() throws Exception {
        // given
        Teacher teacher = teacherRepository.findAll().get(0);
        int size = scheduleRepository.findByTeacher(teacher.getId()).size();

        // when
        mockMvc.perform(get("/teachers/{id}/schedules", teacher.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.length()").value(size))
                .andDo(print());
    }
}
