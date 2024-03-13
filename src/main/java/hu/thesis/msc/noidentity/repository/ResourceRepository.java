package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByName(String name);

}
