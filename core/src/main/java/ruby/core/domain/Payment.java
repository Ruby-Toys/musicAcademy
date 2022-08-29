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

    private String tid;
    private String detail;
    private Long amount;

    @ManyToOne(fetch = LAZY)
    private Student student;

    @Builder
    public Payment(String tid, String detail, Long amount, Student student) {
        this.tid = tid;
        this.detail = detail;
        this.amount = amount;
        this.student = student;
    }
}
