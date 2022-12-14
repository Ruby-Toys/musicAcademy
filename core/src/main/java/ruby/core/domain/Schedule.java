package ruby.core.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ruby.core.domain.enums.ScheduleState;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter @Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    @Enumerated(value = EnumType.STRING)
    private ScheduleState state;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Builder
    public Schedule(LocalDateTime start, LocalDateTime end, ScheduleState state, Teacher teacher, Student student) {
        this.start = start;
        this.end = end;
        this.state = state;
        this.teacher = teacher;
        this.student = student;
    }
}
