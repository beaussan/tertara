package io.nbe.tertara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TertaraApplication {

    public static void main(String[] args) {
        SpringApplication.run(TertaraApplication.class, args);
    }
}
