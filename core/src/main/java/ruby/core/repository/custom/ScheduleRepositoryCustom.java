package ruby.core.repository.custom;

import ruby.core.domain.Schedule;
import ruby.core.domain.enums.Course;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepositoryCustom {

    List<Schedule> findByCourseAndWeek(Course course, LocalDateTime appointmentTime);

    List<Schedule> findByStudent(Long id);

    List<Schedule> findByTeacher(Long id);

    Optional<Schedule> findByIdWithStudent(Long id);

    boolean existsByTime(LocalDateTime start, LocalDateTime end, Course course);

    List<Schedule> findByTomorrow();
}
