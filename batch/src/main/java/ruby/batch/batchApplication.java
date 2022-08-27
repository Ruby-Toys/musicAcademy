package ruby.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EntityScan("ruby.core.domain")
//@EnableJpaRepositories("ruby.core.repository")
//@EnableJpaAuditing
@SpringBootApplication
@EnableBatchProcessing
public class batchApplication {

    public static void main(String[] args) {
        SpringApplication.run(batchApplication.class, args);
    }

}
