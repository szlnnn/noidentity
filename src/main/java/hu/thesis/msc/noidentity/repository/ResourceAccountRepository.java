package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.ResourceAccount;
import hu.thesis.msc.noidentity.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ResourceAccountRepository extends JpaRepository<ResourceAccount, Long> {

    Optional<ResourceAccount> findByResourceAndUser(Resource resource, UserAccount userAccount);

    List<ResourceAccount> findAllByOperationInAndProvisionErrorIsNull(List<String> operation);

}
