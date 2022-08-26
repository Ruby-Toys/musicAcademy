package ruby.api.request.schedule;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ruby.api.valid.EmailPattern;
import ruby.api.valid.LocalDateTimePattern;
import ruby.api.valid.NamePattern;
import ruby.api.valid.PhonePattern;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;

@Getter @Setter
@Builder
public class SchedulePatch {

    private Long studentId;
    private Long teacherId;
    @LocalDateTimePattern
    private String start;
    @LocalDateTimePattern
    private String end;
}
