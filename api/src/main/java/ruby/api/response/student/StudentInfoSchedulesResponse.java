package ruby.api.response.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ruby.core.domain.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class StudentInfoSchedulesResponse {

    private List<ScheduleItem> contents;

    public StudentInfoSchedulesResponse(List<Schedule> schedules) {
        this.contents = schedules.stream()
                .map(ScheduleItem::new)
                .collect(Collectors.toList());
    }

    @Getter
    static class ScheduleItem {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime start;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime end;
        private String state;
        private String teacherName;

        public ScheduleItem(Schedule schedule) {
            this.id = schedule.getId();
            this.start = schedule.getStart();
            this.end = schedule.getEnd();
            this.state = schedule.getState().name();
            this.teacherName = schedule.getTeacher().getName();
        }
    }
}
