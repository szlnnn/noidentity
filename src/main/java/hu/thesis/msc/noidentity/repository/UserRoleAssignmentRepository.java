package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.Role;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.entity.UserRoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRoleAssignmentRepository extends JpaRepository<UserRoleAssignment, Long> {

    Optional<UserRoleAssignment> findByRoleAndUser(Role role, UserAccount user);

}
