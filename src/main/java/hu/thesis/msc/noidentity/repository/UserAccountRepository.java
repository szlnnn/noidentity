package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByLogin(String login);
}