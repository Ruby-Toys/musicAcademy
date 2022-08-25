package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ruby.api.response.student.StudentInfoSchedulesResponse;
import ruby.api.response.teacher.TeacherInfoSchedulesResponse;
import ruby.api.response.teacher.TeachersResponse;
import ruby.api.service.ScheduleService;
import ruby.api.service.TeacherService;
import ruby.core.domain.Schedule;
import ruby.core.domain.Teacher;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final ScheduleService scheduleService;

    @GetMapping
    public TeachersResponse getList() {
        List<Teacher> teachers = teacherService.getList();

        // 검색어, 페이지 번호
        return new TeachersResponse(teachers);
    }

    @GetMapping("/{id}/schedules")
    public TeacherInfoSchedulesResponse getSchedules(@PathVariable Long id) {
        List<Schedule> schedules = scheduleService.getListByTeacher(id);
        return new TeacherInfoSchedulesResponse(schedules);
    }
}
