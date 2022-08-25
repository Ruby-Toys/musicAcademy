package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.repository.custom.StudentRepositoryCustom;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentRepositoryCustom {

    List<Student> findByCourseOrderByName(Course course);
}
