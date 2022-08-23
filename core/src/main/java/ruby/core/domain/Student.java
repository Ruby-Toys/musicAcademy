package ruby.core.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
@Getter @Setter
public class Student extends BaseEntity{

    private String name;
    private String email;
    private String phoneNumber;
    @Enumerated(value = EnumType.STRING)
    private Course course;
    @Enumerated(value = EnumType.STRING)
    private Grade grade;
    private String memo;

    @Builder
    public Student(String name, String email, String phoneNumber, Course course, Grade grade, String memo) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.course = course;
        this.grade = grade;
        this.memo = memo;
    }
}
