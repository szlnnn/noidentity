package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.Request;
import hu.thesis.msc.noidentity.entity.RequestTask;
import hu.thesis.msc.noidentity.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RequestTaskRepository extends JpaRepository<RequestTask, Long> {


    List<RequestTask> findAllByStatusAndApprover(String string, UserAccount approver);

    List<RequestTask> findAllByParentRequest(Request request);

}
