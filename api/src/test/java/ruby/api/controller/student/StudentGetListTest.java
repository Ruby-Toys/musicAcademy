package ruby.api.controller.student;

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
import ruby.api.request.student.StudentSearch;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.StudentRepository;

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
class StudentGetListTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void before() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("권한이 없는 계정으로 수강생 목록 조회")
    @WithMockUser(username = "test", roles = "WAITING")
    void getList_wrongRole() throws Exception {
        // given
        String word = "tes";
        int page = 3;
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
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 하지 않은 상태에서 수강생 목록 조회")
    void getList_noneAccount() throws Exception {
        // given
        String word = "tes";
        int page = 3;
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
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 형식의 페이지 번호로 수강생 목록 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList_wrongPage() throws Exception {
        // given
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
                        .param("page", "asd")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("페이지 번호 없이 수강생 목록 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList_nonePage() throws Exception {
        // given
        String word = "tes";
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
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.totalCount").value(totalCount))
                .andExpect(jsonPath("$.pageSize").value(StudentSearch.PAGE_SIZE))
                .andExpect(jsonPath("$.contents.length()").value(StudentSearch.PAGE_SIZE))
                .andDo(print());
    }


    @Test
    @DisplayName("검색어 없이 수강생 목록 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList_emptyWord() throws Exception {
        // given
        int page = 1;
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
                        .param("page", String.valueOf(page))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.totalCount").value(totalCount))
                .andExpect(jsonPath("$.pageSize").value(StudentSearch.PAGE_SIZE))
                .andExpect(jsonPath("$.contents.length()").value(StudentSearch.PAGE_SIZE))
                .andDo(print());
    }

    @Test
    @DisplayName("수강생 목록 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
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
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.totalCount").value(totalCount))
                .andExpect(jsonPath("$.pageSize").value(StudentSearch.PAGE_SIZE))
                .andExpect(jsonPath("$.contents.length()").value(9))
                .andDo(print());
    }
}