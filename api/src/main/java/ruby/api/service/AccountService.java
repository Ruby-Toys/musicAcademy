package ruby.api.service;

import ruby.api.request.account.AccountLogin;
import ruby.core.domain.Account;

import java.util.List;

public interface AccountService {

    Account signUp(String name, String password);

    void login(AccountLogin accountLogin);

    List<Account> getList();

}
