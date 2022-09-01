package ruby.api.request.schedule;

import lombok.Builder;
import lombok.Getter;
import ruby.api.valid.LocalDateTimePattern;

@Getter
@Builder
public class SchedulePost {

    private Long studentId;
    private Long teacherId;
    @LocalDateTimePattern
    private String start;
    @LocalDateTimePattern
    private String end;
}
