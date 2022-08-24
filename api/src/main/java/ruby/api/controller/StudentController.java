package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ruby.api.request.student.StudentAdd;
import ruby.api.request.student.StudentUpdate;
import ruby.api.request.student.StudentSearch;
import ruby.api.response.student.StudentInfoScheduleResponse;
import ruby.api.response.student.StudentsResponse;
import ruby.api.service.ScheduleService;
import ruby.api.service.StudentService;
import ruby.core.domain.Schedule;
import ruby.core.domain.Student;

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
    public void post(@RequestBody @Valid StudentAdd studentAdd) {
        log.info("post!");
        studentService.add(studentAdd);
    }

    @GetMapping
    public StudentsResponse getList(@Valid StudentSearch studentSearch) {
        Page<Student> studentPage = studentService.getList(studentSearch);
        return new StudentsResponse(studentPage);
    }

    @GetMapping("/{id}/schedules")
    public StudentInfoScheduleResponse getSchedules(@PathVariable Long id) {
        List<Schedule> schedules = scheduleService.getListByStudent(id);
        return new StudentInfoScheduleResponse(schedules);
    }

    @PatchMapping("/{id}")
    public void patch(@PathVariable Long id, @RequestBody @Valid StudentUpdate studentUpdate) {
        studentService.edit(id, studentUpdate);
    }
}
