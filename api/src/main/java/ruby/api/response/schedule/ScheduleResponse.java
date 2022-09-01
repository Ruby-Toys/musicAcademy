package ruby.api.response.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import ruby.core.domain.Schedule;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponse {

    private Long id;
    private Long studentId;
    private Long teacherId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime end;
    private String state;
    private String studentName;
    private String teacherName;

    public ScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.studentId = schedule.getStudent().getId();
        this.teacherId = schedule.getTeacher().getId();
        this.start = schedule.getStart();
        this.end = schedule.getEnd();
        this.state = schedule.getState().name();
        this.studentName = schedule.getStudent().getName();
        this.teacherName = schedule.getTeacher().getName();
    }
}
