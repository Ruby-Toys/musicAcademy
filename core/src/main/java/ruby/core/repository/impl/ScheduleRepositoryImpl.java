package ruby.core.repository.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import ruby.core.domain.*;
import ruby.core.repository.custom.ScheduleRepositoryCustom;
import ruby.core.repository.custom.StudentRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

import static ruby.core.domain.QSchedule.*;
import static ruby.core.domain.QStudent.student;
import static ruby.core.domain.QTeacher.*;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Schedule> findByWeek(LocalDateTime start, LocalDateTime end) {

        return queryFactory.selectFrom(schedule)
                .leftJoin(schedule.student, student).fetchJoin()
                .leftJoin(schedule.teacher, teacher).fetchJoin()
                .where(schedule.appointmentTime.between(start, end))
                .fetch();
    }
}
