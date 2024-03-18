package hu.thesis.msc.noidentity.config;

import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.enums.Role;
import hu.thesis.msc.noidentity.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Configuration
public class UserAccountConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserAccountRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
           UserAccount noadmin =
                   new UserAccount(1L,"No",
                           "Admin",
                           Role.ADMIN,
                           "noadmin", passwordEncoder.encode("Password123"), "admin@email.hu", new Date(), new Date(0, Calendar.JANUARY,10));

            repository.saveAll(
                    List.of(noadmin));
        };
    }

}
