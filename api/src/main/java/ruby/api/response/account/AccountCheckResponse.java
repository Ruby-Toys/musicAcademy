package ruby.api.response.account;

import lombok.Getter;

@Getter
public class AccountCheckResponse {

    private boolean state;

    public AccountCheckResponse(boolean state) {
        this.state = state;
    }
}
