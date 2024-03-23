package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.RequestTask;
import hu.thesis.msc.noidentity.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RequestTaskRepository extends JpaRepository<RequestTask, Long> {


    List<RequestTask> findAllByStatusAndAndApprover(String string, UserAccount approver);

}
