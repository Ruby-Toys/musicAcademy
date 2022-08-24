package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruby.api.request.schedule.ScheduleSearch;
import ruby.core.domain.Schedule;
import ruby.core.domain.enums.Course;
import ruby.core.repository.ScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.DayOfWeek.SUNDAY;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public List<Schedule> getList(ScheduleSearch search) {
        LocalDateTime now = LocalDateTime.parse(search.getAppointmentTime() + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        LocalDateTime start = SUNDAY.equals(dayOfWeek) ? now : now.minusDays(dayOfWeek.ordinal() - 1);
        LocalDateTime end = SUNDAY.equals(dayOfWeek) ?  now.plusDays(SUNDAY.ordinal() + 1) : now.plusDays(SUNDAY.ordinal() - dayOfWeek.ordinal());
        // 스케쥴은 시작날짜와 끝날짜를 입력받아 검색한다.
        return scheduleRepository.findByCourseAndWeek(Course.valueOf(search.getCourse()), start, end);
    }

    public List<Schedule> getListByStudent(Long id) {

        return scheduleRepository.findByStudent(id);
    }
}
