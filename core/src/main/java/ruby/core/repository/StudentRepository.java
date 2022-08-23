package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Student;
import ruby.core.repository.custom.StudentRepositoryCustom;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentRepositoryCustom {
}
