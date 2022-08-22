package ruby.core.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Schedule extends BaseEntity{

    @ManyToOne(fetch = LAZY)
    private Teacher teacher;
    @ManyToOne(fetch = LAZY)
    private Student student;

    @Builder
    public Schedule(LocalDateTime createAt, Teacher teacher, Student student) {
        super.setCreateAt(createAt);
        this.teacher = teacher;
        this.student = student;
    }
}
