package ruby.api.request.teacher;

import lombok.Builder;
import lombok.Getter;
import ruby.api.valid.CoursePattern;
import ruby.api.valid.EmailPattern;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PhonePattern;

@Getter
@Builder
public class TeacherPatch {

    @NamePattern
    private String name;
    @EmailPattern
    private String email;
    @PhonePattern
    private String phoneNumber;
    @CoursePattern
    private String course;
}
