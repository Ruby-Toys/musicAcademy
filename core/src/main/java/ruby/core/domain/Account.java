package ruby.core.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ruby.core.domain.enums.AccountRole;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
@Getter
public class Account extends BaseEntity {

    private String name;
    private String password;
    @Enumerated(value = EnumType.STRING)
    private AccountRole role;

    @Builder
    public Account(String name, String password, AccountRole role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public boolean isWaiting() {
        return this.role.equals(AccountRole.WAITING);
    }

    public boolean isAdmin() {
        return this.role.equals(AccountRole.ADMIN);
    }
}
