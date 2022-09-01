package ruby.api.controller.account;

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
import ruby.core.domain.Account;
import ruby.core.domain.enums.AccountRole;
import ruby.core.repository.AccountRepository;

import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountGetListTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void before() {
        accountRepository.deleteAll();
        IntStream.range(0, 5)
                .forEach(idx -> {
                    Account account = Account.builder()
                            .name("test" + idx)
                            .password(passwordEncoder.encode("12!@qwQW"))
                            .role(AccountRole.ADMIN)
                            .build();
                    accountRepository.save(account);
                } );

    }

    @Test
    @DisplayName("로그인 하지 않은 상태로 계정 목록 조회")
    void getList_notLogin() throws Exception {
        // when
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("매니저 권한으로 계정 목록 조회")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void getList_ManagerRole() throws Exception {
        // when
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("계정 목록 조회")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getList() throws Exception {
        // when
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.length()").value(5))
                .andDo(print());
    }
}