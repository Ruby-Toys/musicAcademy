package ruby.api.response.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ruby.core.domain.Account;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class AccountsResponse {

    private List<AccountResponse> contents;

    public AccountsResponse(List<Account> accounts) {
        this.contents = accounts.stream()
                .map(AccountResponse::new)
                .collect(Collectors.toList());
    }
}
