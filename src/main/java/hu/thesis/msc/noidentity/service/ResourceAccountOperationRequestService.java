package hu.thesis.msc.noidentity.service;


import hu.thesis.msc.noidentity.entity.Request;
import hu.thesis.msc.noidentity.entity.ResourceAccountOperationRequest;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.repository.ResourceAccountOperationRequestRepository;
import hu.thesis.msc.noidentity.repository.ResourceAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RequiredArgsConstructor
@Service
@Transactional
public class ResourceAccountOperationRequestService {

    private final ResourceAccountOperationRequestRepository operationRequestRepository;

    private final ResourceAccountRepository resourceAccountRepository;

    private final ResourceAccountService resourceAccountService;
    public void createOperationRequest(Request request) {
        ResourceAccountOperationRequest operationRequest = new ResourceAccountOperationRequest();
        operationRequest.setResource(request.getRole().getResource());
        operationRequest.setCreateTime(new Date());
        operationRequest.setUser(request.getTargetUser());
        operationRequest.setStatus("N");
        operationRequestRepository.save(operationRequest);
    }


    public void executeNewOperationRequests() {
        operationRequestRepository.findAllByStatus("N").forEach(this::handleNewOperationRequest);
    }


    public void handleNewOperationRequest(ResourceAccountOperationRequest operationRequest) {
        operationRequest.setStatus("R");
        operationRequestRepository.save(operationRequest);
        try {
            resourceAccountRepository.findByResourceAndUser(operationRequest.getResource(), operationRequest.getUser())
                    .ifPresentOrElse(resourceAccountService::updateResourceAccount, () -> resourceAccountService.createResourceAccount(operationRequest));
        } catch (Exception e) {
            operationRequest.setLastError(e.getMessage());
            operationRequest.setStatus("F");
            operationRequestRepository.save(operationRequest);
            throw new AppException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        operationRequest.setStatus("S");
        operationRequest.setCompletionTime(new Date());
        operationRequestRepository.save(operationRequest);
    }


}
