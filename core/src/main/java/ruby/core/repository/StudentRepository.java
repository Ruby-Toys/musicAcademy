package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
