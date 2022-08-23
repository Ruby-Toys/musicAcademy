package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ruby.api.request.student.StudentSearch;
import ruby.api.response.student.StudentsResponse;
import ruby.api.service.StudentService;
import ruby.core.domain.Student;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public StudentsResponse getList(@Valid StudentSearch studentSearch) {
        Page<Student> studentPage = studentService.getList(studentSearch);

        // 검색어, 페이지 번호
        return new StudentsResponse(studentPage);
    }
}
