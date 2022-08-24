package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.request.student.StudentSearch;
import ruby.core.domain.Student;
import ruby.core.repository.StudentRepository;

import java.util.Optional;

import static java.lang.Math.max;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public Page<Student> getList(StudentSearch search) {
        Pageable pageable = PageRequest.of(max(0, search.getPage() - 1), StudentSearch.PAGE_SIZE);
        return studentRepository.findByNameContains(search.getWord(), pageable);
    }

    @Transactional(readOnly = true)
    public Student get(Long id) {
        // 상세 조회를 할 때 회원의 예약된 스케줄도 같이 조회를 한다.
        return studentRepository.findInfo(id)
                .orElseThrow(StudentNotFoundException::new);
    }
}
