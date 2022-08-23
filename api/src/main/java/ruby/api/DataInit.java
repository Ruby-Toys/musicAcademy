package ruby.api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ruby.core.domain.Account;
import ruby.core.domain.Student;
import ruby.core.domain.enums.AccountRole;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;
import ruby.core.repository.AccountRepository;
import ruby.core.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
@Profile("local")
public class DataInit implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initAccount();
        initStudents();
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
                            .name("test" + ++idx)
                            .course(Course.VIOLIN)
                            .email("test" + idx + "@naver.com")
                            .grade(Grade.BEGINNER)
                            .phoneNumber("010-1111-2222")
                            .build()
                    )
                    .collect(Collectors.toList());
            studentRepository.saveAll(students);
        }
    }
}
