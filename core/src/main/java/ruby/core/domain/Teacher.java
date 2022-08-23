package ruby.core.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ruby.core.domain.enums.Course;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
@Getter
public class Teacher extends BaseEntity{

    private String name;
    private String email;
    private String phoneNumber;
    @Enumerated(value = EnumType.STRING)
    private Course course;

    @Builder
    public Teacher(String name, String email, String phoneNumber, Course course) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.course = course;
    }
}
