package com.example.fintechledger;

import com.example.fintechledger.model.User;
import com.example.fintechledger.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EntityScan(basePackages = "com.example.fintechledger.model")
@EnableJpaRepositories(basePackages = "com.example.fintechledger.repository")
public class FintechLedgerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FintechLedgerApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(new User("admin", encoder.encode("admin123"), "ADMIN"));
                System.out.println("Admin user created (username: admin, password: admin123)");
            }
            if (userRepository.findByUsername("user").isEmpty()) {
                userRepository.save(new User("user", encoder.encode("user123"), "USER"));
                System.out.println("User created (username: user, password: user123)");
            }
        };
    }
}
