package hu.thesis.msc.noidentity.scheduledjob;

import hu.thesis.msc.noidentity.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcessNewRequestsJob {

    private final RequestService requestService;

    public ProcessNewRequestsJob(RequestService requestService) {
        this.requestService = requestService;
    }


    @Scheduled(cron = "0/5 * * * * *")
    public void execute() {
        requestService.processNewRequests();
    }

}
