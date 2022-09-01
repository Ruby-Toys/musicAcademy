package ruby.api.request.schedule;

import lombok.Builder;
import lombok.Getter;
import ruby.api.valid.CoursePattern;
import ruby.api.valid.LocalDateTimePattern;

@Getter
@Builder
public class ScheduleSearch {

    @CoursePattern
    private String course;
    @LocalDateTimePattern
    private String appointmentTime;
}
