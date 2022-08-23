package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.core.domain.Teacher;
import ruby.core.repository.TeacherRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public List<Teacher> getList() {
        // 선생님 수는 많지 않으므로 페이징 하지 않고 전체 조회한다.
        return teacherRepository.findByOrderByCourseAscCreateAtDesc();
    }
}
