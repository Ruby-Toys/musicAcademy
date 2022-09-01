package ruby.api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ruby.api.request.account.AccountLogin;
import ruby.api.request.account.AccountSignUp;
import ruby.api.response.account.AccountCheckResponse;
import ruby.api.response.account.AccountResponse;
import ruby.api.response.account.AccountsResponse;
import ruby.api.service.AccountService;
import ruby.core.domain.Account;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/signUp")
    public void signUp(@RequestBody @Valid AccountSignUp accountSignUp) {
        accountService.signUp(accountSignUp.getName(), accountSignUp.getPassword());
    }

    @PostMapping("/login")
    public void login(@RequestBody @Valid AccountLogin accountLogin) {
        accountService.login(accountLogin);
    }

    @PostMapping("/loginCheck")
    public AccountCheckResponse check(Principal principal) {
        return new AccountCheckResponse(principal != null);
    }

    @GetMapping("/accounts")
    public AccountsResponse getList() {
        List<Account> accounts = accountService.getList();
        return new AccountsResponse(accounts);
    }
}
