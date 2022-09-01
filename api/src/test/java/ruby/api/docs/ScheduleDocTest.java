package ruby.api.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.request.schedule.SchedulePatch;
import ruby.api.request.schedule.SchedulePost;
import ruby.api.request.schedule.ScheduleSearch;
import ruby.api.utils.DateUtils;
import ruby.api.valid.validator.ScheduleStateValidator;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "test", roles = "MANAGER")
@AutoConfigureRestDocs(uriHost = "musicAcademy")
@ExtendWith(RestDocumentationExtension.class)
@Transactional
public class ScheduleDocTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void before() {
        scheduleRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("스케줄 등록")
    void postSchedule() throws Exception {
        // given
        Student student = Student.builder()
                .name("student")
                .course(Course.VIOLIN)
                .email("student@naver.com")
                .grade(Grade.BEGINNER)
                .phoneNumber("01011112222")
                .memo("악기 연주에 소질이 있음")
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
                .andDo(document("schedule-post",
                        requestFields(
                                fieldWithPath("start").description("스케줄 시작시간")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("end").description("스케줄 종료시간")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("studentId").description("수강생 순번"),
                                fieldWithPath("teacherId").description("선생님 순번")
                        )
                ));
    }

    @Test
    @DisplayName("스케쥴 조회")
    void getList() throws Exception {
        // given
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
        List<Schedule> schedules = IntStream.range(0, 5)
                .mapToObj(idx -> {
                    LocalDateTime start = LocalDateTime.of(
                            now.getYear(), now.getMonthValue(), now.getDayOfMonth() + (idx * 6), 10 + idx, 0);

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
        String appointmentTime = LocalDateTime.now().format(DateUtils.localDateTimeFormatter());

        ScheduleSearch search = ScheduleSearch.builder()
                .course(Course.VIOLIN.name())
                .appointmentTime(appointmentTime)
                .build();

        // when
        mockMvc.perform(get("/schedules")
                        .param("course", search.getCourse())
                        .param("appointmentTime", search.getAppointmentTime())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("schedule-getList",
                        requestParameters(
                                parameterWithName("course").description("수강과목"),
                                parameterWithName("appointmentTime").description("검색 날짜")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss"))
                        ),
                        responseFields(
                                fieldWithPath("contents[].id").description("스케줄 순번"),
                                fieldWithPath("contents[].studentId").description("수강생 순번"),
                                fieldWithPath("contents[].teacherId").description("선생님 순번"),
                                fieldWithPath("contents[].start").description("스케줄 시작시간")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("contents[].end").description("스케줄 종료시간")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("contents[].state").description("스케줄 상태")
                                        .attributes(key("constraint").value(getScheduleStateConstraintString())),
                                fieldWithPath("contents[].studentName").description("수강생 이름"),
                                fieldWithPath("contents[].teacherName").description("선생님 이름")
                        )
                ));
    }

    @Test
    @DisplayName("스케줄 수정")
    void edit() throws Exception {
        // given
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
        LocalDateTime start = LocalDateTime.of(
                now.getYear(), now.getMonthValue() - 1, 16, 0, 0);

        Schedule schedule = Schedule.builder()
                .start(start)
                .end(start.plusHours(1))
                .state(ScheduleState.NOT_STARTED)
                .student(student)
                .teacher(teacher)
                .build();
        scheduleRepository.save(schedule);
        Teacher newTeacher = Teacher.builder()
                .name("newTeacher")
                .email("qewe@naver.com")
                .phoneNumber("010-1111-2222")
                .course(student.getCourse())
                .build();
        teacherRepository.save(newTeacher);

        DateTimeFormatter formatter = DateUtils.localDateTimeFormatter();
        SchedulePatch schedulePatch = SchedulePatch.builder()
                .studentId(student.getId())
                .teacherId(newTeacher.getId())
                .start(LocalDate.now().atTime(20, 0).format(formatter))
                .end(LocalDate.now().atTime(20, 0).plusHours(1).format(formatter))
                .scheduleState(ScheduleState.COMPLETED.name())
                .build();

        // when
        mockMvc.perform(patch("/schedules/{id}", schedule.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(schedulePatch))
                )
                .andExpect(status().isOk())
                .andDo(document("schedule-patch",
                        pathParameters(
                                parameterWithName("id").description("순번")
                        ),
                        requestFields(
                                fieldWithPath("studentId").description("수강생 순번"),
                                fieldWithPath("teacherId").description("선생님 순번"),
                                fieldWithPath("start").description("스케줄 시작시간")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("end").description("스케줄 종료시간")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("scheduleState").description("스케줄 상태")
                                        .attributes(key("constraint").value(getScheduleStateConstraintString()))
                        )));
    }

    @Test
    @DisplayName("스케줄 삭제")
    void deleteSchedule() throws Exception {
        // given
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
                .state(ScheduleState.NOT_STARTED)
                .student(student)
                .teacher(teacher)
                .build();
        scheduleRepository.save(schedule);

        // when
        mockMvc.perform(delete("/schedules/{id}", schedule.getId()))
                .andExpect(status().isOk())
                .andDo(document("schedule-delete",
                        pathParameters(
                                parameterWithName("id").description("순번")
                        )
                ));
    }

    private String getScheduleStateConstraintString() {
        return ScheduleStateValidator.getRegexpState().replaceAll("\\|", " / ");
    }
}
