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
import ruby.api.request.teacher.TeacherPatch;
import ruby.api.request.teacher.TeacherPost;
import ruby.api.valid.validator.CourseValidator;
import ruby.api.valid.validator.GradeValidator;
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

import java.time.LocalDateTime;
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
public class TeacherDocTest {
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
    @DisplayName("????????? ?????? ??????")
    void postTeacher() throws Exception {
        // given
        TeacherPost teacher = TeacherPost.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE.name())
                .build();

        // when
        mockMvc.perform(post("/teachers")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(teacher))
                )
                .andExpect(status().isOk())
                .andDo(document("teacher-post",
                        requestFields(
                                fieldWithPath("name").description("??????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("phoneNumber").description("????????? ??????"),
                                fieldWithPath("course").description("????????????")
                                        .attributes(key("constraint").value(getCourseConstraintString()))
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void getList() throws Exception {
        // given
        List<Teacher> teachers = IntStream.range(0, 5)
                .mapToObj(idx -> Teacher.builder()
                        .name("teacher" + ++idx)
                        .course(Course.VIOLIN)
                        .email("teacher" + idx + "@naver.com")
                        .phoneNumber("01011112222")
                        .build()
                )
                .collect(Collectors.toList());
        teacherRepository.saveAll(teachers);

        mockMvc.perform(get("/teachers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("teacher-getList",
                        responseFields(
                                fieldWithPath("contents[].id").description("??????"),
                                fieldWithPath("contents[].name").description("??????"),
                                fieldWithPath("contents[].email").description("?????????"),
                                fieldWithPath("contents[].phoneNumber").description("????????? ??????"),
                                fieldWithPath("contents[].course").description("????????????")
                                        .attributes(key("constraint").value(getCourseConstraintString())),
                                fieldWithPath("contents[].createAt").description("?????????")
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ????????? ??????")
    void getSchedules() throws Exception {
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
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = IntStream.range(0, 4)
                .mapToObj(idx -> {
                    LocalDateTime start = LocalDateTime.of(
                            now.getYear(), now.getMonth(), (idx + 1) * 6, 10 + idx, 0);

                    return Schedule.builder()
                            .start(start)
                            .end(start.plusHours(1))
                            .state(idx < 2 ? ScheduleState.COMPLETED : ScheduleState.NOT_STARTED)
                            .student(student)
                            .teacher(teacher)
                            .build();
                })
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
        schedules = IntStream.range(0, 4)
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

        // when
        mockMvc.perform(get("/teachers/{id}/schedules", teacher.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("teacher-getSchedules",
                        pathParameters(
                                parameterWithName("id").description("??????")
                        ),
                        responseFields(
                                fieldWithPath("contents[].id").description("??????"),
                                fieldWithPath("contents[].start").description("????????? ????????????")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("contents[].end").description("????????? ????????????")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("contents[].state").description("????????? ????????????")
                                        .attributes(key("constraint").value(getScheduleStateConstraintString())),
                                fieldWithPath("contents[].studentName").description("????????? ??????"),
                                fieldWithPath("contents[].grade").description("????????? ??????")
                                        .attributes(key("constraint").value(getGradeConstraintString()))
                        )
                ));
    }


    @Test
    @DisplayName("?????????????????? ???????????? ????????? ??????")
    void schedulesByCourse() throws Exception {
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
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = IntStream.range(0, 4)
                .mapToObj(idx -> {
                    LocalDateTime start = LocalDateTime.of(
                            now.getYear(), now.getMonth(), (idx + 1) * 6, 10 + idx, 0);

                    return Schedule.builder()
                            .start(start)
                            .end(start.plusHours(1))
                            .state(idx < 2 ? ScheduleState.COMPLETED : ScheduleState.NOT_STARTED)
                            .student(student)
                            .teacher(teacher)
                            .build();
                })
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
        schedules = IntStream.range(0, 4)
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
        
        // when
        mockMvc.perform(get("/teachers/course")
                        .param("course", Course.VIOLIN.name())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("teacher-schedulesByCourse",
                        requestParameters(
                                parameterWithName("course").description("????????????")
                                        .attributes(key("constraint").value(getCourseConstraintString())).optional()
                        ),
                        responseFields(
                                fieldWithPath("contents[].id").description("??????"),
                                fieldWithPath("contents[].name").description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void edit() throws Exception {
        // given
        Teacher teacher = Teacher.builder()
                .name("teacher")
                .course(Course.VIOLIN)
                .email("teacher@naver.com")
                .phoneNumber("01011112222")
                .build();
        teacherRepository.save(teacher);
        TeacherPatch teacherPatch = TeacherPatch.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE.name())
                .build();

        // when
        mockMvc.perform(patch("/teachers/{id}", teacher.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(teacherPatch))
                )
                .andExpect(status().isOk())
                .andDo(document("teacher-patch",
                        pathParameters(
                                parameterWithName("id").description("??????")
                        ),
                        requestFields(
                                fieldWithPath("name").description("??????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("phoneNumber").description("????????? ??????"),
                                fieldWithPath("course").description("????????????")
                                        .attributes(key("constraint").value(getCourseConstraintString()))
                        )));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void deleteTeacher() throws Exception {
        // given
        Teacher teacher = Teacher.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .build();
        teacherRepository.save(teacher);

        // when
        mockMvc.perform(delete("/teachers/{id}", teacher.getId()))
                .andExpect(status().isOk())
                .andDo(document("teacher-delete",
                        pathParameters(
                                parameterWithName("id").description("??????")
                        )
                ));
    }

    private String getCourseConstraintString() {
        return CourseValidator.getRegexpCourse().replaceAll("\\|", " / ");
    }

    private String getGradeConstraintString() {
        return GradeValidator.getRegexpGrade().replaceAll("\\|", " / ");
    }

    private String getScheduleStateConstraintString() {
        return ScheduleStateValidator.getRegexpState().replaceAll("\\|", " / ");
    }
}
