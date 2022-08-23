package ruby.api.response.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ruby.core.domain.Schedule;

import java.time.LocalDateTime;

@Getter @Setter
public class ScheduleResponse {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime appointmentTime;
    private String state;
    private String studentName;
    private String teacherName;

    public ScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.appointmentTime = schedule.getAppointmentTime();
        this.state = schedule.getState().name();
        this.studentName = schedule.getStudent().getName();
        this.teacherName = schedule.getTeacher().getName();
    }
}
