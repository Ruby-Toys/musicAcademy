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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.account.UserBadCredentialsException;
import ruby.api.exception.account.UserRoleWaitingException;
import ruby.api.request.account.AccountLogin;
import ruby.core.domain.Account;
import ruby.core.domain.enums.AccountRole;
import ruby.core.repository.AccountRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountLoginTest {

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
    @DisplayName("존재하지 않는 계정 로그인")
    void login_noneAccount() throws Exception {
        // given
        Account account = Account.builder()
                .name("admin")
                .password(passwordEncoder.encode("12!@qwQW"))
                .role(AccountRole.ADMIN)
                .build();
        accountRepository.save(account);

        AccountLogin accountLogin = AccountLogin.builder()
                .name(account.getName() + "123")
                .password("12!@qwQW")
                .build();

        // when
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountLogin))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("message").value(UserBadCredentialsException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("일치하지 않는 비밀번호로 계정 로그인")
    void login_wrongPassword() throws Exception {
        // given
        Account account = Account.builder()
                .name("admin")
                .password(passwordEncoder.encode("12!@qwQW"))
                .role(AccountRole.ADMIN)
                .build();
        accountRepository.save(account);

        AccountLogin accountLogin = AccountLogin.builder()
                .name(account.getName())
                .password("12!@qwQWasdasd")
                .build();

        // when
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountLogin))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("message").value(UserBadCredentialsException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("사용 대기중인 계정으로 로그인")
    void login_waitingRole() throws Exception {
        // given
        Account account = Account.builder()
                .name("admin")
                .password(passwordEncoder.encode("12!@qwQW"))
                .role(AccountRole.WAITING)
                .build();
        accountRepository.save(account);

        AccountLogin accountLogin = AccountLogin.builder()
                .name(account.getName())
                .password("12!@qwQW")
                .build();

        // when
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountLogin))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").value(UserRoleWaitingException.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("계정 로그인")
    void login() throws Exception {
        // given
        Account account = Account.builder()
                .name("admin")
                .password(passwordEncoder.encode("12!@qwQW"))
                .role(AccountRole.ADMIN)
                .build();
        accountRepository.save(account);

        AccountLogin accountLogin = AccountLogin.builder()
                .name(account.getName())
                .password("12!@qwQW")
                .build();

        // when
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountLogin))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}
