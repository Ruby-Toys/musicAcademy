package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ruby.api.request.student.StudentPost;
import ruby.api.request.student.StudentPatch;
import ruby.api.request.student.StudentSearch;
import ruby.api.response.student.StudentInfoSchedulesResponse;
import ruby.api.response.student.StudentsResponse;
import ruby.api.response.student.StudentsByCourseResponse;
import ruby.api.service.ScheduleService;
import ruby.api.service.StudentService;
import ruby.core.domain.Schedule;
import ruby.core.domain.Student;
import ruby.core.domain.enums.Course;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ScheduleService scheduleService;

    @PostMapping
    public void post(@RequestBody @Valid StudentPost studentPost) {
        studentService.add(studentPost);
    }

    @GetMapping
    public StudentsResponse getList(@Valid StudentSearch studentSearch) {
        Page<Student> studentPage = studentService.getList(studentSearch);
        return new StudentsResponse(studentPage);
    }

    @GetMapping("/course")
    public StudentsByCourseResponse getListByCourse(Course course) {
        List<Student> students = studentService.getListByCourse(course);
        return new StudentsByCourseResponse(students);
    }

    @GetMapping("/{id}/schedules")
    public StudentInfoSchedulesResponse getSchedules(@PathVariable Long id) {
        List<Schedule> schedules = scheduleService.getListByStudent(id);
        return new StudentInfoSchedulesResponse(schedules);
    }

    @PatchMapping("/{id}")
    public void patch(@PathVariable Long id, @RequestBody @Valid StudentPatch studentPatch) {
        studentService.update(id, studentPatch);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
