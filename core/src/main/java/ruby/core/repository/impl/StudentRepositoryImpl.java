package ruby.core.repository.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import ruby.core.domain.QSchedule;
import ruby.core.domain.QStudent;
import ruby.core.domain.Student;
import ruby.core.repository.custom.StudentRepositoryCustom;

import java.util.List;
import java.util.Optional;

import static ruby.core.domain.QSchedule.*;
import static ruby.core.domain.QStudent.*;

@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Student> findByNameContains(String word, Pageable pageable) {

        List<Student> students = queryFactory.selectFrom(student)
                .where(searchCondition(word))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(student.createAt.desc())
                .fetch();

        int size = queryFactory.selectFrom(student)
                .where(searchCondition(word))
                .fetch()
                .size();

        return PageableExecutionUtils.getPage(students, pageable, () -> size);
    }

    private Predicate searchCondition(String word) {
        return word == null ? null : student.name.contains(word);
    }
}
