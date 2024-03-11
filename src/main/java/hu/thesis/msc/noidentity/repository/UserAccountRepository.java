package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.NoIdMUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoIdMUserRepository extends JpaRepository<NoIdMUser, Long> {

}
