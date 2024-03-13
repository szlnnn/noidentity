package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.entity.AzureResourceConfig;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.repository.AzureResourceConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AzureConfigService {

    private final AzureResourceConfigRepository azureResourceConfigRepository;


    public AzureResourceConfig createAzureConfig(AzureResourceConfig config) {
        Optional<AzureResourceConfig> existingConfig = azureResourceConfigRepository.findByApplicationId(config.getApplicationId());
        if (existingConfig.isPresent()) {
            throw new AppException("Azure config already exists", HttpStatus.BAD_REQUEST);
        }
        return azureResourceConfigRepository.save(config);
    }

}
