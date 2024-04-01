package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.dto.AzureRoleObjectDto;
import hu.thesis.msc.noidentity.entity.AzureResourceConfig;
import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.ResourceAttributeValue;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.repository.ResourceAttributeValueRepository;
import hu.thesis.msc.noidentity.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ResourceService {

    private final AzureConfigService azureConfigService;

    private final ResourceRepository resourceRepository;

    private final ResourceAttributeValueRepository attributeValueRepository;

    private final AzureResourceService azure;
    
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

        if (resource.getAzureConfig() != null) {
            AzureResourceConfig azureConfig = resource.getAzureConfig();
            azureConfig.setApplicationId(resourceFromClient.getAzureConfig().getApplicationId());
            azureConfig.setTenantId(resourceFromClient.getAzureConfig().getTenantId());
            azureConfig.setScope(resourceFromClient.getAzureConfig().getScope());
            azureConfig.setSecret(resourceFromClient.getAzureConfig().getSecret());
            resource.setAzureConfig(azureConfig);
        }

        resource.setAppOwner(resourceFromClient.getAppOwner());
        resourceRepository.save(resource);

        return resource;

    }

    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new AppException("Cannot find resource with id: " + id, HttpStatus.BAD_REQUEST));
    }



    public ResourceAttributeValue updateManagedValue(ResourceAttributeValue attributeValue) {
        return attributeValueRepository.save(attributeValue);
    }

    public List<ResourceAttributeValue> getAttributeValuesForOnlineResources() {
        List<ResourceAttributeValue> result = new ArrayList<>();
        resourceRepository.findAllByType("Azure").stream()
                .map(this::fetchAttributeValuesForResource)
                .forEach(result::addAll);
        return result;
    }


    public List<ResourceAttributeValue> fetchAttributeValuesForResource(Resource resource) {
        List<AzureRoleObjectDto> licenceDtos = azure.getLicences(resource);
        List<AzureRoleObjectDto> directoryRoleDtos = azure.getDirectoryRoles(resource);
        
        licenceDtos.forEach(dto -> handleDto(resource, dto, "Licence"));
        directoryRoleDtos.forEach(dto -> handleDto(resource, dto, "ApplicationRole"));

        return attributeValueRepository.findAllByResource(resource);

    }

    private void handleDto(Resource resource, AzureRoleObjectDto dto, String type) {
        if (!attributeValueRepository.existsByIdentifierAndResource(dto.getUid(), resource)) {
            ResourceAttributeValue attributeValue = new ResourceAttributeValue();
            attributeValue.setResource(resource);
            attributeValue.setIdentifier(dto.getUid());
            attributeValue.setName(dto.getName());
            attributeValue.setManaged(false);
            attributeValue.setType(type);
            attributeValueRepository.save(attributeValue);
        }
    }


}
