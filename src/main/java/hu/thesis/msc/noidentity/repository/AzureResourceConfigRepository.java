package hu.thesis.msc.noidentity.repository;

import hu.thesis.msc.noidentity.entity.AzureResourceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AzureResourceConfigRepository extends JpaRepository<AzureResourceConfig, Long> {

    Optional<AzureResourceConfig> findByApplicationId(String applicationId);
}
