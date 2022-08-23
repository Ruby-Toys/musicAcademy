package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.function.SingletonSupplier;
import ruby.api.exception.account.UserBadCredentialsException;
import ruby.api.exception.account.UserRoleWaitingException;
import ruby.api.exception.account.UsernameSameException;
import ruby.api.request.account.AccountLogin;
import ruby.api.security.UserAccount;
import ruby.core.domain.Account;
import ruby.core.domain.enums.AccountRole;
import ruby.core.repository.AccountRepository;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;

    @Transactional
    public Account signUp(String name, String password) {
        Optional<Account> optionalAccount = accountRepository.findByName(name);
        if (optionalAccount.isPresent()) throw new UsernameSameException();

        Account account = Account.builder()
                .name(name)
                .password(passwordEncoder.encode(password))
                .role(AccountRole.WAITING)
                .build();

        return accountRepository.save(account);
    }


    public void login(AccountLogin accountLogin) {
        Account account = accountRepository.findByName(accountLogin.getName())
                .orElseThrow(UserBadCredentialsException::new);

        if (!passwordEncoder.matches(accountLogin.getPassword(), account.getPassword())) {
            throw new UserBadCredentialsException();
        }

        if (account.isWaiting()) {
            throw new UserRoleWaitingException();
        }

        setAuthentication(account);
    }

    
    public List<Account> getList() {
        return accountRepository.findAll();
    }

    private void setAuthentication(Account account) {
        UserAccount userAccount = new UserAccount(account);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userAccount, account.getPassword(), authorities(account.getRole()));

        httpSession.setAttribute("account", userAccount);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private Collection<? extends GrantedAuthority> authorities(AccountRole role) {
        return Collections.singleton(() -> "ROLE_" + role.name());
    }
}
