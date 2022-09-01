package ruby.api.request.account;

import lombok.Builder;
import lombok.Getter;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PasswordPattern;

@Getter
public class AccountSignUp {

    @NamePattern
    private String name;
    @PasswordPattern
    private String password;

    @Builder
    public AccountSignUp(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
