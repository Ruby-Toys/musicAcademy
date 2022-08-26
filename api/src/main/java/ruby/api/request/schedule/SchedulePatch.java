package ruby.api.request.schedule;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ruby.api.valid.*;
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
    @ScheduleStatePattern
    private String scheduleState;
}
