package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.request.student.StudentPost;
import ruby.api.request.student.StudentPatch;
import ruby.api.request.student.StudentSearch;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.StudentRepository;

import java.util.List;

import static java.lang.Math.max;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public void add(StudentPost studentPost) {
        Student student = Student.builder()
                .name(studentPost.getName())
                .phoneNumber(studentPost.getPhoneNumber())
                .email(studentPost.getEmail())
                .course(Course.valueOf(studentPost.getCourse()))
                .grade(Grade.valueOf(studentPost.getGrade()))
                .memo(studentPost.getMemo())
                .build();
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Page<Student> getList(StudentSearch search) {
        Pageable pageable = PageRequest.of(max(0, search.getPage() - 1), StudentSearch.PAGE_SIZE);
        return studentRepository.findByNameContains(search.getWord(), pageable);
    }

    public List<Student> getListByCourse(Course course) {
        return studentRepository.findByCourseOrderByName(course);
    }

    public void update(Long id, StudentPatch studentPatch) {
        Student student = studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);

        student.setName(studentPatch.getName());
        student.setEmail(studentPatch.getEmail());
        student.setPhoneNumber(studentPatch.getPhoneNumber());
        student.setCourse(Course.valueOf(studentPatch.getCourse()));
        student.setGrade(Grade.valueOf(studentPatch.getGrade()));
        student.setMemo(studentPatch.getMemo());
    }

    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);
        studentRepository.delete(student);
    }
}
