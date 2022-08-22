package ruby.api.request.account;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class AccountLogin {

//    @NamePattern
    private String name;
//    @PasswordPattern
    private String password;

    @Builder
    public AccountLogin(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
