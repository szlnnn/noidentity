package hu.thesis.msc.noidentity.config;

import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.enums.Role;
import hu.thesis.msc.noidentity.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class UserAccountConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserAccountRepository repository) {
        return args -> {
           UserAccount szilcso =
                   new UserAccount(1L,"Csortan",
                           "Szilard",
                           Role.USER,
                           "csoszi@email.hu", "password123");
            UserAccount mohi =
                    new UserAccount(2L,"Bereczki",
                            "Uzonka",
                            Role.ADMIN,
                            "mohi@email.hu", "password123");
            repository.saveAll(
                    List.of(szilcso, mohi));
        };
    }

}
