package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Schedule;
import ruby.core.repository.custom.ScheduleRepositoryCustom;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {
}
