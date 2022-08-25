package ruby.core.domain;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ruby.core.domain.enums.Course;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
@Getter @Setter
public class Teacher extends BaseEntity{

    private String name;
    private String email;
    private String phoneNumber;
    @Enumerated(value = EnumType.STRING)
    private Course course;
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @Builder
    public Teacher(String name, String email, String phoneNumber, Course course) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.course = course;
    }
}
