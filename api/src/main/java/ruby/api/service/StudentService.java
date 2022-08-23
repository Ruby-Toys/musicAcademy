package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.request.student.StudentSearch;
import ruby.core.domain.Student;
import ruby.core.repository.StudentRepository;

import static java.lang.Math.max;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public Page<Student> getList(StudentSearch search) {
        Pageable pageable = PageRequest.of(max(0, search.getPage() - 1), 20);
        return studentRepository.findByNameContains(search.getWord(), pageable);
    }
}
