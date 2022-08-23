package ruby.api.request.schedule;

import lombok.Getter;
import lombok.Setter;
import ruby.api.valid.CoursePattern;
import ruby.api.valid.DatePattern;

@Getter @Setter
public class ScheduleSearch {

    @CoursePattern
    private String course;
    @DatePattern
    private String appointmentTime;
}
