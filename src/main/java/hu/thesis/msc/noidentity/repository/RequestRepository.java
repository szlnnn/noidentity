package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.Request;
import hu.thesis.msc.noidentity.entity.UserRoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByStatus(String status);

    List<Request> findAllByStatusAndAssignment(String status, UserRoleAssignment ura);

}
