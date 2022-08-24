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
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.Course;
import ruby.core.repository.TeacherRepository;

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
class TeacherGetListTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TeacherRepository teacherRepository;

    @BeforeEach
    void before() {
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("선생님 목록 조회")
    @WithMockUser(username = "test", roles = "MANAGER")
    void getList() throws Exception {
        // given
        List<Teacher> teachers = IntStream.range(0, 5)
                .mapToObj(idx -> Teacher.builder()
                        .name("teacher" + ++idx)
                        .course(Course.VIOLIN)
                        .email("teacher" + idx + "@naver.com")
                        .phoneNumber("010-1111-2222")
                        .build()
                )
                .collect(Collectors.toList());
        teacherRepository.saveAll(teachers);

        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.length()").value(teachers.size()))
                .andDo(print());
    }
}