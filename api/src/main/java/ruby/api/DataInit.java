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
        initSchedules();
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
            List<Student> students = IntStream.range(0, 2)
                    .mapToObj(idx -> Student.builder()
                            .name("student" + ++idx)
                            .course(idx % 2 == 0 ? Course.VIOLIN : Course.PIANO)
                            .email("student" + idx + "@naver.com")
                            .grade(Grade.BEGINNER)
                            .phoneNumber("01011112222")
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
            List<Teacher> teachers = IntStream.range(0, 2)
                    .mapToObj(idx -> Teacher.builder()
                            .name("teacher" + ++idx)
                            .course(idx % 2 == 0 ? Course.VIOLIN : Course.PIANO)
                            .email("teacher" + idx + "@naver.com")
                            .phoneNumber("01011112222")
                            .build()
                    )
                    .collect(Collectors.toList());
            teacherRepository.saveAll(teachers);
        }
    }

    public void initSchedules() {
        Student student1 = studentRepository.findAll().get(0);
        Teacher teacher1 = teacherRepository.findAll().get(0);

        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = IntStream.range(0, 4)
                .mapToObj(idx -> Schedule.builder()
                        .appointmentTime(
                                LocalDateTime.of(
                                        now.getYear(), now.getMonth(),
                                        (idx + 1) * 6,  10 + idx, 0))
                        .state(idx < 2 ? ScheduleState.COMPLETED : ScheduleState.NOT_STARTED)
                        .student(student1)
                        .teacher(teacher1)
                        .build()
                )
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);

        Student student2 = studentRepository.findAll().get(1);
        Teacher teacher2 = teacherRepository.findAll().get(1);
        schedules = IntStream.range(0, 4)
                .mapToObj(idx -> Schedule.builder()
                        .appointmentTime(
                                LocalDateTime.of(
                                        now.getYear(), now.getMonth(),
                                        (idx + 1) * 6,  10 + idx, 0))
                        .state(idx < 2 ? ScheduleState.COMPLETED : ScheduleState.NOT_STARTED)
                        .student(student2)
                        .teacher(teacher2)
                        .build()
                )
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
    }
}
