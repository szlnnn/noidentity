package hu.thesis.msc.noidentity.service;


import hu.thesis.msc.noidentity.dto.ResponseFromResourceDto;
import hu.thesis.msc.noidentity.entity.ResourceAccount;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.repository.ResourceAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ProvisionService {

    private final ResourceAccountRepository resourceAccountRepository;

    private final SimulatedResourceService simulatedResourceService;

    private final AzureResourceService azureResourceService;

    private final RequestService requestService;

    public void handleProvisionTasks() {
        resourceAccountRepository.findAllByOperationInAndProvisionErrorIsNull(getProvisionOperations())
                .forEach(this::handleProvisionTask);
    }

    private void handleProvisionTask(ResourceAccount resourceAccount) {
        ResponseFromResourceDto response = null;
        try {
            if ("C".equals(resourceAccount.getOperation())) {
                response = doCreate(resourceAccount);
            } else if ("U".equals(resourceAccount.getOperation())){
                response = doUpdate(resourceAccount);
            } else {
                throw new UnsupportedOperationException("Should not be calling provision on resource account: " + resourceAccount.getIdentifier());
            }
        } catch (Exception e) {
            resourceAccount.setProvisionError(e.getMessage());
            resourceAccountRepository.save(resourceAccount);
        }
        if(response != null) {
            resourceAccount.setLastProvisionTime(new Date());
            resourceAccount.setOperation(null);
            resourceAccount.setProvisionError(null);
            resourceAccount.setIdentifier(response.getUid());
            resourceAccount.setAttributesOnResource(response.getAttributesOnResource());
            resourceAccount.setReconStatus("CONFIRMED");
            resourceAccountRepository.save(resourceAccount);
            requestService.afterProvisionListener(resourceAccount);
        }
    }

    private ResponseFromResourceDto doCreate(ResourceAccount resourceAccount) {
        ResponseFromResourceDto dto;
        if ("Offline".equals(resourceAccount.getResource().getType())) {
            dto = simulatedResourceService.create(resourceAccount);
        } else if ("Azure".equals(resourceAccount.getResource().getType())) {
            dto = azureResourceService.create(resourceAccount);
        } else {
            throw new IllegalStateException("Cannot find resource type: " + resourceAccount.getResource().getType());
        }
        return dto;
    }

    private ResponseFromResourceDto doUpdate(ResourceAccount resourceAccount) {
        ResponseFromResourceDto dto;
        if ("Offline".equals(resourceAccount.getResource().getType())) {
            dto = simulatedResourceService.update(resourceAccount);
        } else if ("Azure".equals(resourceAccount.getResource().getType())) {
            dto = azureResourceService.update(resourceAccount);
        } else {
            throw new IllegalStateException("Cannot find resource type: " + resourceAccount.getResource().getType());
        }
        return dto;
    }


    private List<String> getProvisionOperations() {
        return List.of("C","U");
    }

}
