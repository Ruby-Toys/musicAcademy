package ruby.api.response.teacher;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import ruby.core.domain.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TeacherInfoSchedulesResponse {

    private List<ScheduleItem> contents;

    public TeacherInfoSchedulesResponse(List<Schedule> schedules) {
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
        private String studentName;
        private String grade;

        public ScheduleItem(Schedule schedule) {
            this.id = schedule.getId();
            this.start = schedule.getStart();
            this.start = schedule.getEnd();
            this.state = schedule.getState().name();
            this.studentName = schedule.getStudent().getName();
            this.grade = schedule.getStudent().getGrade().name();
        }
    }
}
