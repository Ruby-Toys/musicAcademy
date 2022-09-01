package ruby.api.response.account;

import lombok.Getter;
import ruby.core.domain.Account;

import java.time.format.DateTimeFormatter;

@Getter
public class AccountResponse {

    private String name;
    private String role;
    private String createAt;

    public AccountResponse(Account account) {
        this.name = account.getName();
        this.role = account.getRole().name();
        this.createAt = account.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
