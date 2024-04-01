package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.ResourceAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ResourceAttributeValueRepository extends JpaRepository<ResourceAttributeValue, Long> {


    List<ResourceAttributeValue> findAllByResource(Resource resource);

    boolean existsByIdentifierAndResource(String identifier, Resource resource);

}
