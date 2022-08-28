package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ruby.api.request.account.AccountLogin;
import ruby.api.service.AccountService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/")
    public String index() {
        return "ok";
    }

    @PostMapping("/login")
    public void login(@RequestBody @Valid AccountLogin accountLogin) {
        accountService.login(accountLogin);
    }

    @PostMapping("/loginCheck")
    public boolean check(Principal principal) {
        return principal != null;
    }
}
