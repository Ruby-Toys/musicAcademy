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
import ruby.api.exception.account.UserSameException;
import ruby.api.request.account.AccountSignUp;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PasswordPattern;
import ruby.core.domain.Account;
import ruby.core.domain.enums.AccountRole;
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
public class AccountSignUpTest {

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
    @DisplayName("잘못된 값으로 계정 등록")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void signUp_WrongFields() throws Exception {
        // given
        AccountSignUp accountSignUp = AccountSignUp.builder()
                .name("test!@#")
                .password("qefsf dsfsf")
                .build();

        // when
        mockMvc.perform(post("/signUp")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountSignUp))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ExceptionController.BIND_EXCEPTION_MESSAGE))
                .andExpect(jsonPath("$.validation.name").value(NamePattern.MESSAGE))
                .andExpect(jsonPath("$.validation.password").value(PasswordPattern.MESSAGE))
                .andDo(print());
    }

    @Test
    @DisplayName("중복된 이름으로 계정 등록")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void signUp_isExistsSameName() throws Exception {
        // given
        Account account = Account.builder()
                .name("test")
                .password(passwordEncoder.encode("12!@qwQW"))
                .role(AccountRole.WAITING)
                .build();
        accountRepository.save(account);

        AccountSignUp accountSignUp = AccountSignUp.builder()
                .name("test")
                .password("12!@qwQW")
                .build();

        // when
        mockMvc.perform(post("/signUp")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountSignUp))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(UserSameException.MESSAGE))
                .andDo(print());
    }


    @Test
    @DisplayName("매니저 권한으로 계정 등록")
    @WithMockUser(username = "admin", roles = "MANAGER")
    void signUp_ManagerRole() throws Exception {
        // given
        AccountSignUp accountSignUp = AccountSignUp.builder()
                .name("test")
                .password("12!@qwQW")
                .build();

        // when
        mockMvc.perform(post("/signUp")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountSignUp))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("계정 등록")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void signUp() throws Exception {
        // given
        AccountSignUp accountSignUp = AccountSignUp.builder()
                .name("test")
                .password("12!@qwQW")
                .build();

        // when
        mockMvc.perform(post("/signUp")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountSignUp))
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Account account = accountRepository.findAll().get(0);
        assertThat(account.getName()).isEqualTo(accountSignUp.getName());
        assertThat(passwordEncoder.matches(accountSignUp.getPassword(), account.getPassword())).isTrue();
        assertThat(account.isAdmin()).isFalse();
        assertThat(account.isWaiting()).isTrue();
    }
}
