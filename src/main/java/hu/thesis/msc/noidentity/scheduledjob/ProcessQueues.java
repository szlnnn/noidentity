package hu.thesis.msc.noidentity.scheduledjob;

import hu.thesis.msc.noidentity.service.RequestService;
import hu.thesis.msc.noidentity.service.ResourceAccountOperationRequestService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcessQueues {

    private final RequestService requestService;

    private final ResourceAccountOperationRequestService resourceAccountOperationRequestService;

    public ProcessQueues(RequestService requestService, ResourceAccountOperationRequestService resourceAccountOperationRequestService) {
        this.requestService = requestService;
        this.resourceAccountOperationRequestService = resourceAccountOperationRequestService;
    }

    @Scheduled(cron = "0/5 * * * * *")
    public void processNewRequests() {
        requestService.processNewRequests();
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void processNewRequestOperations() {
        resourceAccountOperationRequestService.executeNewOperationRequests();
    }


}
