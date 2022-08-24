package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ruby.api.request.student.StudentSearch;
import ruby.api.response.schedule.SchedulesResponse;
import ruby.api.response.student.StudentsResponse;
import ruby.api.service.ScheduleService;
import ruby.api.service.StudentService;
import ruby.core.domain.Schedule;
import ruby.core.domain.Student;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ScheduleService scheduleService;

    @GetMapping
    public StudentsResponse getList(@Valid StudentSearch studentSearch) {
        Page<Student> studentPage = studentService.getList(studentSearch);

        // 검색어, 페이지 번호
        return new StudentsResponse(studentPage);
    }

    @GetMapping("/{id}/schedules")
    public SchedulesResponse getSchedules(@PathVariable Long id) {
        List<Schedule> schedules = scheduleService.getListByStudent(id);

        // 검색어, 페이지 번호
        return new SchedulesResponse(schedules);
    }
}
