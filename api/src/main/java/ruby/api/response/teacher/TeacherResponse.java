package ruby.api.response.teacher;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import ruby.core.domain.Teacher;

import java.time.LocalDateTime;

@Getter
public class TeacherResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String course;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createAt;


    public TeacherResponse(Teacher teacher) {
        this.id = teacher.getId();
        this.name = teacher.getName();
        this.email = teacher.getEmail();
        this.phoneNumber = teacher.getPhoneNumber();
        this.course = teacher.getCourse().name();
        this.createAt = teacher.getCreateAt();
    }
}
