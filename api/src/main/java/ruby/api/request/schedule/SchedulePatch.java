package ruby.api.request.schedule;

import lombok.Builder;
import lombok.Getter;
import ruby.api.valid.LocalDateTimePattern;
import ruby.api.valid.ScheduleStatePattern;

@Getter
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
