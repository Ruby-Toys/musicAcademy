package ruby.api.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ruby.core.repository.AccountRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountLogoutTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void before() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인하지 않은 상태로 로그아웃 요청")
    void logout_notLoginState() throws Exception {
        // when
        mockMvc.perform(post("/logout"))
                .andExpect(status().isFound())
                .andDo(print());
    }

    @Test
    @DisplayName("계정 로그아웃")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void logout() throws Exception {
        // when
        mockMvc.perform(post("/logout"))
                .andExpect(status().isFound())
                .andDo(print());
    }
}
