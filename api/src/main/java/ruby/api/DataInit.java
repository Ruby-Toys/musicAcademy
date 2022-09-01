package ruby.api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ruby.core.domain.Account;
import ruby.core.domain.enums.AccountRole;
import ruby.core.repository.*;

/**
 * 더미 데이터 생성
 */
@RequiredArgsConstructor
@Component
public class DataInit implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initAccount();
    }

    public void initAccount() {
        accountRepository.deleteAll();

        Account admin = Account.builder()
                .name("admin")
                .password(passwordEncoder.encode("12!@qwQW"))
                .role(AccountRole.ADMIN)
                .build();

        accountRepository.save(admin);

        Account manager = Account.builder()
                .name("test")
                .password(passwordEncoder.encode("12!@qwQW"))
                .role(AccountRole.MANAGER)
                .build();

        accountRepository.save(manager);
    }
}
