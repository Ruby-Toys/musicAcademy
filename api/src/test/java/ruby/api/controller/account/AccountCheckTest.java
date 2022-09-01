package ruby.api.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.controller.ExceptionController;
import ruby.api.request.account.AccountSignUp;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PasswordPattern;
import ruby.core.domain.Account;
import ruby.core.repository.AccountRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountCheckTest {

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
    @DisplayName("로그인 상태가 아닐 때 상태 체크")
    void loginCheck_noneLoginState() throws Exception {
        // when
        mockMvc.perform(post("/loginCheck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 상태 체크")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void loginCheck() throws Exception {
        // when
        mockMvc.perform(post("/loginCheck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(true))
                .andDo(print());
    }
}
