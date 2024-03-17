package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role>  findAllByResource(Resource resource);

}
