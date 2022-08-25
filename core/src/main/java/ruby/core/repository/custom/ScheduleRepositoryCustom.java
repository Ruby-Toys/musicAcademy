package ruby.core.repository.custom;

import ruby.core.domain.Schedule;
import ruby.core.domain.enums.Course;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepositoryCustom {

    List<Schedule> findByCourseAndWeek(Course course, LocalDateTime appointmentTime);

    List<Schedule> findByStudent(Long id);

    List<Schedule> findByTeacher(Long id);
}
