package ruby.core.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
@Getter @Setter
public class Payment extends BaseEntity{

    private Long amount;

    @ManyToOne(fetch = LAZY)
    private Student student;

    @Builder
    public Payment(Long amount, Student student) {
        this.amount = amount;
        this.student = student;
    }
}
