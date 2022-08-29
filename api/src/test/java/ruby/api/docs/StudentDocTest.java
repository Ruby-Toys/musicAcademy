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
    @DisplayName("수강생 정보 등록")
    void postStudent() throws Exception {
        // given
        StudentPost student = StudentPost.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE.name())
                .grade(Grade.BEGINNER.name())
                .memo("수강생 신규 등록")
                .build();

        // when
        mockMvc.perform(post("/students")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(student))
                )
                .andExpect(status().isOk())
                .andDo(document("student-post",
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("phoneNumber").description("핸드폰 번호"),
                                fieldWithPath("course").description("수강과목")
                                        .attributes(key("constraint").value(getCourseConstraintString())),
                                fieldWithPath("grade").description("수강등급")
                                        .attributes(key("constraint").value(getGradeConstraintString())),
                                fieldWithPath("memo").description("메모").optional()
                        )
                ));
    }

    @Test
    @DisplayName("수강생 목록 조회")
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
                                parameterWithName("word").description("검색어").optional(),
                                parameterWithName("page").description("페이지 번호").optional()
                        ),
                        responseFields(
                                fieldWithPath("page").description("조회 페이지"),
                                fieldWithPath("pageSize").description("페이지 당 조회 개수"),
                                fieldWithPath("totalCount").description("검색 결과 총 개수"),
                                fieldWithPath("contents[].id").description("순번"),
                                fieldWithPath("contents[].name").description("이름"),
                                fieldWithPath("contents[].email").description("이메일"),
                                fieldWithPath("contents[].phoneNumber").description("핸드폰 번호"),
                                fieldWithPath("contents[].course").description("수강과목")
                                        .attributes(key("constraint").value(getCourseConstraintString())),
                                fieldWithPath("contents[].grade").description("수강등급")
                                        .attributes(key("constraint").value(getGradeConstraintString())),
                                fieldWithPath("contents[].memo").description("메모"),
                                fieldWithPath("contents[].createAt").description("등록일")
                        )
                ));
    }

    @Test
    @DisplayName("수강생의 스케쥴 조회")
    void getSchedules() throws Exception {
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
                                parameterWithName("id").description("순번")
                        ),
                        responseFields(
                                fieldWithPath("contents[].id").description("순번"),
                                fieldWithPath("contents[].start").description("스케줄 시작시간")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("contents[].end").description("스케줄 종료시간")
                                        .attributes(key("constraint").value("yyyy-MM-dd HH:mm:ss")),
                                fieldWithPath("contents[].state").description("스케줄 진행상태")
                                        .attributes(key("constraint").value(getScheduleStateConstraintString())),
                                fieldWithPath("contents[].teacherName").description("선생님 이름")
                        )
                ));
    }

    @Test
    @DisplayName("수강과목으로 수강생의 스케줄 조회")
    void schedulesByCourse() throws Exception {
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
                                parameterWithName("course").description("수강과목")
                                        .attributes(key("constraint").value(getCourseConstraintString())).optional()
                        ),
                        responseFields(
                                fieldWithPath("contents[].id").description("순번"),
                                fieldWithPath("contents[].name").description("이름")
                        )
                ));
    }

    @Test
    @DisplayName("수강생 정보 수정")
    void edit() throws Exception {
        // given
        Student student = Student.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .grade(Grade.BEGINNER)
                .memo("수강생 신규 등록")
                .build();
        studentRepository.save(student);
        StudentPatch studentPatch = StudentPatch.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE.name())
                .grade(Grade.ADVANCED.name())
                .memo("고급 단계로 변경")
                .build();

        // when
        mockMvc.perform(patch("/students/{id}", student.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(studentPatch))
                )
                .andExpect(status().isOk())
                .andDo(document("student-patch",
                        pathParameters(
                                parameterWithName("id").description("순번")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("phoneNumber").description("핸드폰 번호"),
                                fieldWithPath("course").description("수강과목")
                                        .attributes(key("constraint").value(getCourseConstraintString())),
                                fieldWithPath("grade").description("수강등급")
                                        .attributes(key("constraint").value(getGradeConstraintString())),
                                fieldWithPath("memo").description("메모")
                        )));
    }

    @Test
    @DisplayName("수강생 정보 삭제")
    void deleteStudent() throws Exception {
        // given
        Student student = Student.builder()
                .name("test")
                .email("test@naver.com")
                .phoneNumber("01023233423")
                .course(Course.FLUTE)
                .grade(Grade.BEGINNER)
                .memo("수강생 신규 등록")
                .build();
        studentRepository.save(student);

        // when
        mockMvc.perform(delete("/students/{id}", student.getId()))
                .andExpect(status().isOk())
                .andDo(document("student-delete",
                        pathParameters(
                                parameterWithName("id").description("순번")
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
