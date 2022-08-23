package ruby.api.request.schedule;

import lombok.Getter;
import lombok.Setter;
import ruby.api.valid.DatePattern;

@Getter @Setter
public class ScheduleSearch {

    @DatePattern
    private String time;
}
