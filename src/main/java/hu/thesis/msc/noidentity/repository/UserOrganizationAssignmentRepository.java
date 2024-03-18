package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.Organization;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.entity.UserOrganizationAssignment;
import jakarta.persistence.JoinColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserOrganizationAssignmentRepository extends JpaRepository<UserOrganizationAssignment, Long> {

    Optional<UserOrganizationAssignment> findByUserAndAssignmentType(UserAccount user, String assignmentType);

    Optional<UserOrganizationAssignment> findByOrganizationAndAssignmentType(Organization organization, String assignmentType);

}
