package ruby.api.request.account;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AccountLogin {

    private String name;
    private String password;

    @Builder
    public AccountLogin(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
