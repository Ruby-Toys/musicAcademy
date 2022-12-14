package ruby.core.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.*;

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
    private int remainderCnt;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @Builder
    public Student(String name, String email, String phoneNumber, Course course, Grade grade, String memo, int remainderCnt) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.course = course;
        this.grade = grade;
        this.memo = memo;
        this.remainderCnt = remainderCnt;
    }

    public void addPayment() {
        this.remainderCnt += 4;
    }
    public void cancelPayment() {
        this.remainderCnt -= 4;
    }
    public void makeSchedule() {
        this.remainderCnt--;
    }
    public void cancelSchedule() {
        this.remainderCnt++;
    }
}
