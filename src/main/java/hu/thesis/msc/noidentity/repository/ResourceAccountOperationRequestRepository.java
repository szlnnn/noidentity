package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.ResourceAccountOperationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ResourceAccountOperationRequestRepository extends JpaRepository<ResourceAccountOperationRequest, Long> {


    List<ResourceAccountOperationRequest> findAllByStatus(String status);

}
