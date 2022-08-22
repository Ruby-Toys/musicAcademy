package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
