package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.account.UserBadCredentialsException;
import ruby.api.exception.account.UserRoleWaitingException;
import ruby.api.exception.account.UserSameException;
import ruby.api.request.account.AccountLogin;
import ruby.api.security.UserAccount;
import ruby.core.domain.Account;
import ruby.core.domain.enums.AccountRole;
import ruby.core.repository.AccountRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService  {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(String name, String password) {
        Optional<Account> optionalAccount = accountRepository.findByName(name);
        if (optionalAccount.isPresent()) throw new UserSameException();

        Account account = Account.builder()
                .name(name)
                .password(passwordEncoder.encode(password))
                .role(AccountRole.WAITING)
                .build();

        accountRepository.save(account);
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
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        new UserAccount(account), account.getPassword(), authorities(account.getRole()));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private Collection<? extends GrantedAuthority> authorities(AccountRole role) {
        return Collections.singleton(() -> "ROLE_" + role.name());
    }
}
