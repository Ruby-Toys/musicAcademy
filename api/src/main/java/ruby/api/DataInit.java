package ruby.api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ruby.core.domain.*;
import ruby.core.domain.enums.AccountRole;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.domain.enums.ScheduleState;
import ruby.core.repository.*;

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
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initAccount();
        initStudents();
        initTeachers();
        initSchedules();
        initPayment();
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

        exists = accountRepository.findByName("test").isPresent();

        if (!exists) {
            Account admin = Account.builder()
                    .name("test")
                    .password(passwordEncoder.encode("12!@qwQW"))
                    .role(AccountRole.MANAGER)
                    .build();

            accountRepository.save(admin);
        }
    }

    public void initStudents() {
        boolean notExists = studentRepository.count() == 0;

        if (notExists) {
            List<Student> students = IntStream.range(0, 10)
                    .mapToObj(idx -> Student.builder()
                            .name("student" + ++idx)
                            .course(idx % 2 == 0 ? Course.VIOLIN : Course.PIANO)
                            .email("student" + idx + "@naver.com")
                            .grade(idx % 2 == 0 ? Grade.BEGINNER : Grade.INTERMEDIATE)
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
            List<Teacher> teachers = IntStream.range(0, 10)
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
        List<Student> students = studentRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();

        LocalDateTime now = LocalDateTime.now();
        IntStream.range(0, 2)
                .forEach(index -> {
                    List<Schedule> schedules = IntStream.range(0, 4)
                            .mapToObj(idx -> {
                                LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(),
                                        (idx + 1) * 6,  10 + idx, 0);

                                return Schedule.builder()
                                        .start(start)
                                        .end(start.plusHours(1))
                                        .state(idx < 2 ? ScheduleState.COMPLETED : ScheduleState.NOT_STARTED)
                                        .student(students.get(index))
                                        .teacher(teachers.get(index))
                                        .build();
                            })
                            .collect(Collectors.toList());
                    scheduleRepository.saveAll(schedules);
                });
    }

    public void initPayment() {
        List<Student> students = studentRepository.findAll();

        for (Student student : students) {
            List<Payment> payments = IntStream.range(0, 15)
                    .mapToObj(idx -> {
                        Payment payment = Payment.builder()
                                .student(student)
                                .amount(student.getGrade().getAmount())
                                .build();
                        payment.setCreateAt(LocalDateTime.now().minusMonths(idx));
                        return payment;
                    })
                    .collect(Collectors.toList());
            paymentRepository.saveAll(payments);
        }
    }
}
