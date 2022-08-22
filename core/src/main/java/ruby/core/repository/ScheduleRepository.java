package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
