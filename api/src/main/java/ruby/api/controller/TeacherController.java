package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ruby.api.request.student.StudentPost;
import ruby.api.request.teacher.TeacherPatch;
import ruby.api.request.teacher.TeacherPost;
import ruby.api.response.student.StudentsByCourseResponse;
import ruby.api.response.teacher.TeacherInfoSchedulesResponse;
import ruby.api.response.teacher.TeachersByCourseResponse;
import ruby.api.response.teacher.TeachersResponse;
import ruby.api.service.ScheduleService;
import ruby.api.service.TeacherService;
import ruby.core.domain.Schedule;
import ruby.core.domain.Student;
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.Course;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final ScheduleService scheduleService;

    @PostMapping
    public void post(@RequestBody @Valid TeacherPost teacherPost) {
        teacherService.add(teacherPost);
    }

    @GetMapping
    public TeachersResponse getList() {
        List<Teacher> teachers = teacherService.getList();
        return new TeachersResponse(teachers);
    }

    @GetMapping("/course")
    public TeachersByCourseResponse getListByCourse(Course course) {
        List<Teacher> teachers = teacherService.getListByCourse(course);
        return new TeachersByCourseResponse(teachers);
    }

    @GetMapping("/{id}/schedules")
    public TeacherInfoSchedulesResponse getSchedules(@PathVariable Long id) {
        List<Schedule> schedules = scheduleService.getListByTeacher(id);
        return new TeacherInfoSchedulesResponse(schedules);
    }

    @PatchMapping("/{id}")
    public void patch(@PathVariable Long id, @RequestBody @Valid TeacherPatch teacherPatch) {
        teacherService.update(id, teacherPatch);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teacherService.delete(id);
    }
}
