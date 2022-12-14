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
import ruby.api.request.student.StudentPatch;
import ruby.api.request.student.StudentPost;
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
public class StudentDocTest {
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
    void postStudent() throws Exception {
        // given
        StudentPost student = StudentPost.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE.name())
                .grade(Grade.BEGINNER.name())
                .memo("????????? ?????? ??????")
                .build();

        // when
        mockMvc.perform(post("/students")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(student))
                )
                .andExpect(status().isOk())
                .andDo(document("student-post",
                        requestFields(
                                fieldWithPath("name").description("??????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("phoneNumber").description("????????? ??????"),
                                fieldWithPath("course").description("????????????")
                                        .attributes(key("constraint").value(getCourseConstraintString())),
                                fieldWithPath("grade").description("????????????")
                                        .attributes(key("constraint").value(getGradeConstraintString())),
                                fieldWithPath("memo").description("??????").optional()
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void getList() throws Exception {
        // given
        String word = "tes";
        int page = 4;
        int totalCount = 54;
        List<Student> students = IntStream.range(0, totalCount)
                .mapToObj(idx -> Student.builder()
                        .name("test" + ++idx)
                        .course(Course.VIOLIN)
                        .email("test" + idx + "@naver.com")
                        .grade(Grade.BEGINNER)
                        .phoneNumber("01011112222")
                        .build()
                )
                .collect(Collectors.toList());
        studentRepository.saveAll(students);


        // when
        mockMvc.perform(get("/students")
                        .param("word", word)
                        .param("page", String.valueOf(page))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("student-getList",
                        requestParameters(
                                parameterWithName("word").description("?????????").optional(),
                                parameterWithName("page").description("????????? ??????").optional()
                        ),
                        responseFields(
                                fieldWithPath("page").description("?????? ?????????"),
                                fieldWithPath("pageSize").description("????????? ??? ?????? ??????"),
                                fieldWithPath("totalCount").description("?????? ?????? ??? ??????"),
                                fieldWithPath("contents[].id").description("??????"),
                                fieldWithPath("contents[].name").description("??????"),
                                fieldWithPath("contents[].email").description("?????????"),
                                fieldWithPath("contents[].phoneNumber").description("????????? ??????"),
                                fieldWithPath("contents[].course").description("????????????")
                                        .attributes(key("constraint").value(getCourseConstraintString())),
                                fieldWithPath("contents[].grade").description("????????????")
                                        .attributes(key("constraint").value(getGradeConstraintString())),
                                fieldWithPath("contents[].memo").description("??????"),
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
        mockMvc.perform(get("/students/{id}/schedules", student.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("student-getSchedules",
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
                                fieldWithPath("contents[].teacherName").description("????????? ??????")
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
        mockMvc.perform(get("/students/course")
                        .param("course", Course.VIOLIN.name())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("student-schedulesByCourse",
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
        Student student = Student.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .grade(Grade.BEGINNER)
                .memo("????????? ?????? ??????")
                .build();
        studentRepository.save(student);
        StudentPatch studentPatch = StudentPatch.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE.name())
                .grade(Grade.ADVANCED.name())
                .memo("?????? ????????? ??????")
                .build();

        // when
        mockMvc.perform(patch("/students/{id}", student.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(studentPatch))
                )
                .andExpect(status().isOk())
                .andDo(document("student-patch",
                        pathParameters(
                                parameterWithName("id").description("??????")
                        ),
                        requestFields(
                                fieldWithPath("name").description("??????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("phoneNumber").description("????????? ??????"),
                                fieldWithPath("course").description("????????????")
                                        .attributes(key("constraint").value(getCourseConstraintString())),
                                fieldWithPath("grade").description("????????????")
                                        .attributes(key("constraint").value(getGradeConstraintString())),
                                fieldWithPath("memo").description("??????")
                        )));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void deleteStudent() throws Exception {
        // given
        Student student = Student.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .grade(Grade.BEGINNER)
                .memo("????????? ?????? ??????")
                .build();
        studentRepository.save(student);

        // when
        mockMvc.perform(delete("/students/{id}", student.getId()))
                .andExpect(status().isOk())
                .andDo(document("student-delete",
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
