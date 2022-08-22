package ruby.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.account.UserBadCredentialsException;
import ruby.api.exception.account.UserRoleWaitingException;
import ruby.api.exception.account.UsernameSameException;
import ruby.api.request.account.AccountLogin;
import ruby.api.security.UserAccount;
import ruby.api.service.AccountService;
import ruby.core.domain.Account;
import ruby.core.domain.enums.AccountRole;
import ruby.core.repository.AccountRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
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

    @Override
    @Transactional(readOnly = true)
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

    @Override
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
        return List.of(new SimpleGrantedAuthority(role.name()));
    }


}
