package ruby.core.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import ruby.core.domain.Schedule;
import ruby.core.domain.enums.Course;
import ruby.core.repository.custom.ScheduleRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

import static ruby.core.domain.QSchedule.schedule;
import static ruby.core.domain.QStudent.student;
import static ruby.core.domain.QTeacher.teacher;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Schedule> findByCourseAndWeek(Course course, LocalDateTime start, LocalDateTime end) {

        return queryFactory.selectFrom(schedule)
                .leftJoin(schedule.student, student).fetchJoin()
                .leftJoin(schedule.teacher, teacher).fetchJoin()
                .where(
                        schedule.student.course.eq(course),
                        schedule.appointmentTime.goe(start),
                        schedule.appointmentTime.lt(end))
                .fetch();
    }

    @Override
    public List<Schedule> findByStudent(Long id) {
        return queryFactory.selectFrom(schedule)
                .leftJoin(schedule.teacher, teacher).fetchJoin()
                .where(schedule.student.id.eq(id))
                .fetch();
    }
}
