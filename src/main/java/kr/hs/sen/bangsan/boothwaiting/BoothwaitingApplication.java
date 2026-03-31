package kr.hs.sen.bangsan.boothwaiting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BoothwaitingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoothwaitingApplication.class, args);
    }

}
