package hu.thesis.msc.noidentity.scheduledjob;

import hu.thesis.msc.noidentity.service.ProvisionService;
import hu.thesis.msc.noidentity.service.RequestService;
import hu.thesis.msc.noidentity.service.ResourceAccountOperationRequestService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcessQueues {

    private final RequestService requestService;

    private final ResourceAccountOperationRequestService resourceAccountOperationRequestService;

    private final ProvisionService provisionService;

    public ProcessQueues(RequestService requestService,
                         ResourceAccountOperationRequestService resourceAccountOperationRequestService,
                         ProvisionService provisionService) {
        this.requestService = requestService;
        this.resourceAccountOperationRequestService = resourceAccountOperationRequestService;
        this.provisionService = provisionService;
    }

    @Scheduled(cron = "0/5 * * * * *")
    public void processNewRequests() {
        requestService.processNewRequests();
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void processNewRequestOperations() {
        resourceAccountOperationRequestService.executeNewOperationRequests();
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void processProvision() {
        provisionService.handleProvisionTasks();
    }

}
