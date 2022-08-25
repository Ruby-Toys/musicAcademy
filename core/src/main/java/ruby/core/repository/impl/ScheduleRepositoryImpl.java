package ruby.core.repository.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import ruby.core.domain.Schedule;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.ScheduleState;
import ruby.core.repository.custom.ScheduleRepositoryCustom;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ruby.core.domain.QSchedule.schedule;
import static ruby.core.domain.QStudent.student;
import static ruby.core.domain.QTeacher.teacher;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Schedule> findByCourseAndWeek(Course course, LocalDateTime appointmentTime) {

        return queryFactory.selectFrom(schedule)
                .leftJoin(schedule.student, student).fetchJoin()
                .leftJoin(schedule.teacher, teacher).fetchJoin()
                .where(
                        schedule.student.course.eq(course), weekCondition(appointmentTime))
                .fetch();
    }

    @Override
    public List<Schedule> findByStudent(Long id) {
        return queryFactory.selectFrom(schedule)
                .leftJoin(schedule.teacher, teacher).fetchJoin()
                .where(schedule.student.id.eq(id), monthCondition(LocalDateTime.now()))
                .orderBy(schedule.id.desc())
                .fetch();
    }

    @Override
    public List<Schedule> findByTeacher(Long id) {
        return queryFactory.selectFrom(schedule)
                .leftJoin(schedule.student, student).fetchJoin()
                .where(schedule.teacher.id.eq(id))
                .orderBy(schedule.id.desc())
                .fetch();
    }


    private Predicate weekCondition(LocalDateTime time) {
        DayOfWeek dayOfWeek = time.getDayOfWeek();

        LocalDateTime start = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), 0, 0)
                .minusDays(dayOfWeek.ordinal());
        LocalDateTime end = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), 0, 0)
                .plusDays(7 - dayOfWeek.ordinal());

        return schedule.start.goe(start).and(schedule.end.before(end));
    }


    private Predicate monthCondition(LocalDateTime time) {
        LocalDateTime start = LocalDateTime.of(time.getYear(), time.getMonth(), 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(time.getYear(), time.getMonthValue() + 1, 1, 0, 0);

        return schedule.start.goe(start).and(schedule.end.before(end));
    }
}
