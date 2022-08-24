package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.request.student.StudentAdd;
import ruby.api.request.student.StudentUpdate;
import ruby.api.request.student.StudentSearch;
import ruby.core.domain.Student;
import ruby.core.repository.StudentRepository;

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

    public void add(StudentAdd studentAdd) {
        Student student = Student.builder()
                .name(studentAdd.getName())
                .phoneNumber(studentAdd.getPhoneNumber())
                .email(studentAdd.getEmail())
                .course(studentAdd.getCourse())
                .grade(studentAdd.getGrade())
                .memo(studentAdd.getMemo())
                .build();
        studentRepository.save(student);
    }

    public void edit(Long id, StudentUpdate studentUpdate) {
        Student student = studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);

        student.setName(studentUpdate.getName());
        student.setEmail(studentUpdate.getEmail());
        student.setPhoneNumber(studentUpdate.getPhoneNumber());
        student.setCourse(studentUpdate.getCourse());
        student.setGrade(studentUpdate.getGrade());
        student.setMemo(studentUpdate.getMemo());
    }
}
