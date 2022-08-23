package ruby.core.repository.custom;

import ruby.core.domain.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepositoryCustom {

    List<Schedule> findByWeek(LocalDateTime start, LocalDateTime end);
}
