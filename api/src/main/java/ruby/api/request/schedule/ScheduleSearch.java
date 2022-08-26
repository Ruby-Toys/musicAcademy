package ruby.api.request.schedule;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ruby.api.valid.CoursePattern;
import ruby.api.valid.DatePattern;
import ruby.api.valid.LocalDateTimePattern;
import ruby.core.domain.enums.Course;

@Getter @Setter
@Builder
public class ScheduleSearch {

    @CoursePattern
    private String course;
    @LocalDateTimePattern
    private String appointmentTime;
}
