package ruby.api.request.schedule;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ruby.api.valid.DatePattern;
import ruby.core.domain.enums.Course;

@Getter @Setter
@Builder
public class ScheduleSearch {

    private Course course;
    @DatePattern
    private String appointmentTime;
}
