package ruby.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.Course;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findByOrderByCourseAscCreateAtDesc();

    List<Teacher> findByCourseOrderByName(Course course);
}
