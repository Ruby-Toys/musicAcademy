package ruby.api.request.student;

import lombok.Builder;
import lombok.Getter;
import ruby.api.valid.*;

@Getter
@Builder
public class StudentPost {

    @NamePattern
    private String name;
    @EmailPattern
    private String email;
    @PhonePattern
    private String phoneNumber;
    @CoursePattern
    private String course;
    @GradePattern
    private String grade;
    private String memo;
}
