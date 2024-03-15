package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.entity.AzureResourceConfig;
import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ResourceService {

    private final AzureConfigService azureConfigService;

    private final ResourceRepository resourceRepository;

    public Resource createResource(Resource resourceFromClient) {
        Optional<Resource> resourceOptional = resourceRepository.findByName(resourceFromClient.getName());

        if(resourceOptional.isPresent()) {
            throw new AppException("Resource already exists", HttpStatus.BAD_REQUEST);

        }
        if (resourceFromClient.getAzureConfig() != null && !resourceFromClient.getAzureConfig().getApplicationId().isBlank()) {
            azureConfigService.createAzureConfig(resourceFromClient.getAzureConfig());
        } else {
            resourceFromClient.setAzureConfig(null);
        }
        return resourceRepository.save(resourceFromClient);
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Resource updateResource(Resource resourceFromClient) {
        Optional<Resource> optionalResource = resourceRepository.findByName(resourceFromClient.getName());
        if (optionalResource.isEmpty()) {
            throw new AppException("Cannot find resource with name: " + resourceFromClient.getName(), HttpStatus.BAD_REQUEST);
        }

        Resource resource = optionalResource.get();

        if (resource.getAzureConfig() == null) {
            return resource;
        }
        AzureResourceConfig azureConfig = resource.getAzureConfig();
        azureConfig.setApplicationId(resourceFromClient.getAzureConfig().getApplicationId());
        azureConfig.setTenantId(resourceFromClient.getAzureConfig().getTenantId());
        azureConfig.setScope(resourceFromClient.getAzureConfig().getScope());
        azureConfig.setSecret(resourceFromClient.getAzureConfig().getSecret());

        resource.setAzureConfig(azureConfig);
        resourceRepository.save(resource);

        return resource;

    }
}
