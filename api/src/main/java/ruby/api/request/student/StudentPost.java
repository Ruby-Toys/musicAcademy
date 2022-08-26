package ruby.api.request.student;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ruby.api.valid.*;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;

@Getter @Setter
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
