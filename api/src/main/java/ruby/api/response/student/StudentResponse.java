package ruby.api.response.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ruby.core.domain.Student;

import java.time.LocalDateTime;

@Getter @Setter
public class StudentResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String course;
    private String grade;
    private String memo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createAt;


    public StudentResponse(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.email = student.getEmail();
        this.phoneNumber = student.getPhoneNumber();
        this.course = student.getCourse().name();
        this.grade = student.getGrade().name();
        this.memo = student.getMemo();
        this.createAt = student.getCreateAt();
    }
}
