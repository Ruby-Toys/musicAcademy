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
import ruby.api.exception.schedule.ScheduleNotEqualsDayException;
import ruby.api.exception.schedule.ScheduleNotFoundException;
import ruby.api.exception.PeriodException;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.exception.teacher.TeacherNotFoundException;
import ruby.api.request.schedule.SchedulePatch;
import ruby.api.utils.DateUtils;
import ruby.api.valid.LocalDateTimePattern;
import ruby.api.valid.ScheduleStatePattern;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
public class SchedulePatchTest {

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
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = IntStream.range(0, 4)
                .mapToObj(idx -> {
                    LocalDateTime start = LocalDateTime.of(
                            now.getYear(), now.getMonthValue() - 1, (idx + 1) * 6, 10 + idx, 0);

                    return Schedule.builder()
                            .start(start)
                            .end(start.plusHours(1))
                            .state(ScheduleState.COMPLETED)
                            .student(student)
                            .teacher(teacher)
                            .build();

                })
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
        schedules = IntStream.range(0, 4)
                .mapToObj(idx -> {
                    LocalDateTime start = LocalDateTime.of(
                            now.getYear(), now.getMonth(), (idx + 1) * 6, 10 + idx, 0);

                    return Schedule.builder()
                            .start(start)
                            .end(start.plusHours(1))
                            .state(ScheduleState.NOT_STARTED)
                            .student(student)
                            .teacher(teacher)
                            .build();
                })
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
    }

    @Test
    @DisplayName("존재하지 않는 선생님으로 스케줄 수정")
    void edit_noneTeacher() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = Teacher.builder()
                .name("newTeacher")
                .email("qewe@naver.com")
                .phoneNumber("010-1111-2222")
                .course(student.getCourse())
                .build();
        teacherRepository.save(teacher);

        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        SchedulePatch schedulePatch = SchedulePatch.builder()
                .studentId(student.getId())
                .teacherId(teacher.getId() + 999)
                .start(LocalDateTime.now().format(formatter))
                .end(LocalDateTime.now().plusHours(1).format(formatter))
                .scheduleState(ScheduleState.COMPLETED.name())
                .build();

        // when
        mockMvc.perform(patch("/schedules/{id}", schedule.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePatch))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(TeacherNotFoundException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 수강생의 스케줄 수정")
    void edit_noneStudent() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = Teacher.builder()
                .name("newTeacher")
                .email("qewe@naver.com")
                .phoneNumber("010-1111-2222")
                .course(student.getCourse())
                .build();
        teacherRepository.save(teacher);

        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        SchedulePatch schedulePatch = SchedulePatch.builder()
                .studentId(student.getId() + 999)
                .teacherId(teacher.getId())
                .start(LocalDateTime.now().format(formatter))
                .end(LocalDateTime.now().plusHours(1).format(formatter))
                .scheduleState(ScheduleState.COMPLETED.name())
                .build();

        // when
        mockMvc.perform(patch("/schedules/{id}", schedule.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePatch))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(StudentNotFoundException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 수정")
    void edit_noneSchedule() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = Teacher.builder()
                .name("newTeacher")
                .email("qewe@naver.com")
                .phoneNumber("010-1111-2222")
                .course(student.getCourse())
                .build();
        teacherRepository.save(teacher);

        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        SchedulePatch schedulePatch = SchedulePatch.builder()
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .start(LocalDateTime.now().format(formatter))
                .end(LocalDateTime.now().plusHours(1).format(formatter))
                .scheduleState(ScheduleState.COMPLETED.name())
                .build();

        // when
        mockMvc.perform(patch("/schedules/{id}", schedule.getId() + 999)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePatch))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ScheduleNotFoundException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 필드로 스케줄 수정")
    void edit_wrongField() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = Teacher.builder()
                .name("newTeacher")
                .email("qewe@naver.com")
                .phoneNumber("010-1111-2222")
                .course(student.getCourse())
                .build();
        teacherRepository.save(teacher);

        SchedulePatch schedulePatch = SchedulePatch.builder()
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .start("20222213")
                .end("2011-10-22")
                .scheduleState("BAD")
                .build();

        // when
        mockMvc.perform(patch("/schedules/{id}", schedule.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePatch))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andExpect(jsonPath("$.validation.start").value(LocalDateTimePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.end").value(LocalDateTimePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.scheduleState").value(ScheduleStatePattern.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("스케줄 시작 시간이 종료 시간 보다 이전으로 스케줄 수정")
    void edit_wrongDate() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = Teacher.builder()
                .name("newTeacher")
                .email("qewe@naver.com")
                .phoneNumber("010-1111-2222")
                .course(student.getCourse())
                .build();
        teacherRepository.save(teacher);

        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        SchedulePatch schedulePatch = SchedulePatch.builder()
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .start(LocalDateTime.now().plusHours(1).format(formatter))
                .end(LocalDateTime.now().format(formatter))
                .scheduleState(ScheduleState.COMPLETED.name())
                .build();

        // when
        mockMvc.perform(patch("/schedules/{id}", schedule.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePatch))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(PeriodException.MESSAGE))
                .andDo(print());
    }


    @Test
    @DisplayName("스케줄 시작과 종료가 서로 다른 날로 스케줄 수정")
    void edit_notEqualsDay() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = Teacher.builder()
                .name("newTeacher")
                .email("qewe@naver.com")
                .phoneNumber("010-1111-2222")
                .course(student.getCourse())
                .build();
        teacherRepository.save(teacher);

        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        SchedulePatch schedulePatch = SchedulePatch.builder()
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .start(LocalDateTime.now().format(formatter))
                .end(LocalDateTime.now().plusDays(1).format(formatter))
                .scheduleState(ScheduleState.COMPLETED.name())
                .build();

        // when
        mockMvc.perform(patch("/schedules/{id}", schedule.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePatch))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ScheduleNotEqualsDayException.MESSAGE))
                .andDo(print());
    }


    @Test
    @DisplayName("스케줄 수정")
    void edit() throws Exception {
        // given
        Schedule schedule = scheduleRepository.findAll().get(0);
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = Teacher.builder()
                .name("newTeacher")
                .email("qewe@naver.com")
                .phoneNumber("010-1111-2222")
                .course(student.getCourse())
                .build();
        teacherRepository.save(teacher);

        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        SchedulePatch schedulePatch = SchedulePatch.builder()
                .studentId(student.getId())
                .teacherId(teacher.getId())
                .start(LocalDateTime.now().format(formatter))
                .end(LocalDateTime.now().plusHours(1).format(formatter))
                .scheduleState(ScheduleState.COMPLETED.name())
                .build();

        // when
        mockMvc.perform(patch("/schedules/{id}", schedule.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePatch))
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Schedule savedSchedule = scheduleRepository.findById(schedule.getId()).get();
        assertThat(savedSchedule.getId()).isEqualTo(schedule.getId());
        assertThat(savedSchedule.getStudent().getId()).isEqualTo(student.getId());
        assertThat(savedSchedule.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(savedSchedule.getStart()).isEqualTo(LocalDateTime.parse(schedulePatch.getStart(), formatter));
        assertThat(savedSchedule.getEnd()).isEqualTo(LocalDateTime.parse(schedulePatch.getEnd(), formatter));
        assertThat(savedSchedule.getState()).isEqualTo(ScheduleState.valueOf(schedulePatch.getScheduleState()));
    }
}
