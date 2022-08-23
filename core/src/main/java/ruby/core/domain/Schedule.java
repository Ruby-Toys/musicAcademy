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
@Getter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime appointmentTime;
    @Enumerated(value = EnumType.STRING)
    private ScheduleState state;
    @ManyToOne(fetch = LAZY)
    private Teacher teacher;
    @ManyToOne(fetch = LAZY)
    private Student student;

    @Builder
    public Schedule(LocalDateTime appointmentTime, ScheduleState state, Teacher teacher, Student student) {
        this.appointmentTime = appointmentTime;
        this.state = state;
        this.teacher = teacher;
        this.student = student;
    }
}
