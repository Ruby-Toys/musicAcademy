package ruby.api.response.schedule;

import lombok.Getter;
import ruby.core.domain.Schedule;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SchedulesResponse {

    private List<ScheduleResponse> contents;

    public SchedulesResponse(List<Schedule> schedules) {
        this.contents = schedules.stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }
}
