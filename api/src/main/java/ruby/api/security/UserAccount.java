package ruby.api.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ruby.core.domain.Account;

import java.util.List;

/**
 * Principal 대상 클래스로 사용할 Account 클래스
 */
public class UserAccount extends User {

    public UserAccount(Account account) {
        super(account.getName(), account.getPassword(), List.of(new SimpleGrantedAuthority(account.getRole().name())));
    }
}
