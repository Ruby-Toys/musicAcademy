package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.teacher.TeacherNotFoundException;
import ruby.api.request.teacher.TeacherPatch;
import ruby.api.request.teacher.TeacherPost;
import ruby.core.domain.Student;
import ruby.core.domain.Teacher;
import ruby.core.repository.TeacherRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public void add(TeacherPost teacherPost) {
        Teacher teacher = Teacher.builder()
                .name(teacherPost.getName())
                .phoneNumber(teacherPost.getPhoneNumber())
                .email(teacherPost.getEmail())
                .course(teacherPost.getCourse())
                .build();
        teacherRepository.save(teacher);
    }

    @Transactional(readOnly = true)
    public List<Teacher> getList() {
        // 선생님 수는 많지 않으므로 페이징 하지 않고 전체 조회한다.
        return teacherRepository.findByOrderByCourseAscCreateAtDesc();
    }

    public void update(Long id, TeacherPatch teacherPatch) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(TeacherNotFoundException::new);

        teacher.setName(teacherPatch.getName());
        teacher.setEmail(teacherPatch.getEmail());
        teacher.setPhoneNumber(teacherPatch.getPhoneNumber());
        teacher.setCourse(teacherPatch.getCourse());
    }

    public void delete(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(TeacherNotFoundException::new);
        teacherRepository.delete(teacher);
    }
}
