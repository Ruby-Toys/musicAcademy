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
import ruby.api.exception.schedule.ScheduleNotFoundException;
import ruby.api.request.schedule.ScheduleSearch;
import ruby.api.service.ScheduleService;
import ruby.api.utils.DateUtils;
import ruby.api.valid.CoursePattern;
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

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test", roles = "MANAGER")
class ScheduleDeleteTest {

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

        Schedule schedule = Schedule.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .state(ScheduleState.COMPLETED)
                .student(student)
                .teacher(teacher)
                .build();
        scheduleRepository.save(schedule);
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 삭제")
    void deleteSchedule_noneSchedule() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);

        // when
        mockMvc.perform(delete("/schedules/{id}", schedule.getId() + 999))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ScheduleNotFoundException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("스케줄 삭제")
    void deleteSchedule() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);

        // when
        mockMvc.perform(delete("/schedules/{id}", schedule.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNull();
    }
}