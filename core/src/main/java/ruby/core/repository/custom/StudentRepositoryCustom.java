package ruby.core.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ruby.core.domain.Student;

import java.util.Optional;

public interface StudentRepositoryCustom {

    Page<Student> findByNameContains(String word, Pageable pageable);

    Optional<Student> findInfo(Long id);
}
