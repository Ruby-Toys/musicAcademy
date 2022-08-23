package ruby.api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ruby.core.domain.Account;
import ruby.core.domain.Schedule;
import ruby.core.domain.Student;
import ruby.core.domain.Teacher;
import ruby.core.domain.enums.AccountRole;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.domain.enums.ScheduleState;
import ruby.core.repository.AccountRepository;
import ruby.core.repository.ScheduleRepository;
import ruby.core.repository.StudentRepository;
import ruby.core.repository.TeacherRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 더미 데이터 생성
 */
@RequiredArgsConstructor
@Component
@Profile("local")
public class DataInit implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initAccount();
        initStudents();
        initTeachers();
    }

    public void initAccount() {
        boolean exists = accountRepository.findByName("admin").isPresent();

        if (!exists) {
            Account admin = Account.builder()
                    .name("admin")
                    .password(passwordEncoder.encode("12!@qwQW"))
                    .role(AccountRole.ADMIN)
                    .build();

            accountRepository.save(admin);
        }
    }

    public void initStudents() {
        boolean notExists = studentRepository.count() == 0;

        if (notExists) {
            List<Student> students = IntStream.range(0, 271)
                    .mapToObj(idx -> Student.builder()
                            .name("student" + ++idx)
                            .course(idx % 2 == 0 ? Course.VIOLIN : Course.PIANO)
                            .email("student" + idx + "@naver.com")
                            .grade(Grade.BEGINNER)
                            .phoneNumber("010-1111-2222")
                            .memo("악기 연주에 소질이 있음")
                            .build()
                    )
                    .collect(Collectors.toList());
            studentRepository.saveAll(students);
        }
    }

    public void initTeachers() {
        boolean notExists = teacherRepository.count() == 0;

        if (notExists) {
            List<Teacher> teachers = IntStream.range(0, 5)
                    .mapToObj(idx -> Teacher.builder()
                            .name("teacher" + ++idx)
                            .course(idx % 2 == 0 ? Course.VIOLIN : Course.PIANO)
                            .email("teacher" + idx + "@naver.com")
                            .phoneNumber("010-1111-2222")
                            .build()
                    )
                    .collect(Collectors.toList());
            teacherRepository.saveAll(teachers);
        }
    }

    public void initSchedules() {
        Student student = studentRepository.findAll().get(0);
        Teacher teacher = teacherRepository.findAll().get(0);

        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = IntStream.range(0, 8)
                .mapToObj(idx -> Schedule.builder()
                        .appointmentTime(
                                LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                                        10 + idx, 0))
                        .state(ScheduleState.NOT_STARTED)
                        .student(student)
                        .teacher(teacher)
                        .build()
                )
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
        schedules = IntStream.range(0, 8)
                .mapToObj(idx -> Schedule.builder()
                        .appointmentTime(
                                LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth() + 7,
                                        10 + idx, 0))
                        .state(ScheduleState.NOT_STARTED)
                        .student(student)
                        .teacher(teacher)
                        .build()
                )
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
    }
}
