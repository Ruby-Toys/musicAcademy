package ruby.api.request.student;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ruby.api.valid.EmailPattern;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PhonePattern;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;

@Getter @Setter
@Builder
public class StudentAdd {

    @NamePattern
    private String name;
    @EmailPattern
    private String email;
    @PhonePattern
    private String phoneNumber;
    private Course course;
    private Grade grade;
    private String memo;
}
