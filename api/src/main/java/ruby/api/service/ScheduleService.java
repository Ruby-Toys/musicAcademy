package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.exception.schedule.ScheduleNotFoundException;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.exception.teacher.TeacherNotFoundException;
import ruby.api.request.schedule.SchedulePost;
import ruby.api.request.schedule.ScheduleSearch;
import ruby.core.domain.Schedule;
import ruby.core.domain.Student;
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.ScheduleState;
import ruby.core.repository.ScheduleRepository;
import ruby.core.repository.StudentRepository;
import ruby.core.repository.TeacherRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public Schedule add(SchedulePost schedulePost) {
        Student student = studentRepository.findById(schedulePost.getStudentId())
                .orElseThrow(StudentNotFoundException::new);
        Teacher teacher = teacherRepository.findById(schedulePost.getTeacherId())
                .orElseThrow(TeacherNotFoundException::new);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(schedulePost.getStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(schedulePost.getEnd(), formatter);

        Schedule schedule = Schedule.builder()
                .start(start)
                .end(end)
                .student(student)
                .teacher(teacher)
                .state(ScheduleState.NOT_STARTED)
                .build();
        return scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<Schedule> getList(ScheduleSearch search) {
        LocalDateTime appointmentTime = LocalDateTime.parse(search.getAppointmentTime() + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // 스케쥴은 시작날짜와 끝날짜를 입력받아 검색한다.
        return scheduleRepository.findByCourseAndWeek(search.getCourse(), appointmentTime);
    }

    public List<Schedule> getListByStudent(Long id) {

        return scheduleRepository.findByStudent(id);
    }

    public List<Schedule> getListByTeacher(Long id) {
        return scheduleRepository.findByTeacher(id);
    }

    public void update(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);

        schedule.setState(ScheduleState.COMPLETED);
    }

    public void delete(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(ScheduleNotFoundException::new);
        scheduleRepository.delete(schedule);
    }
}
