package hu.thesis.msc.noidentity.controller;

import hu.thesis.msc.noidentity.dto.ClientRequestDto;
import hu.thesis.msc.noidentity.dto.RequestTaskDto;
import hu.thesis.msc.noidentity.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/request")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping()
    public ResponseEntity<String> sendRequest(@RequestBody ClientRequestDto clientRequestDto) {
        requestService.createRequestsFromBulkRequest(clientRequestDto);
        return ResponseEntity.ok("Successfully posted the requests");
    }
    @GetMapping("/tasks/{id}")
    public List<RequestTaskDto> getTasksForApprover(@PathVariable Long id) {
        return requestService.getTasksForApprover(id);
    }

    @GetMapping("/task/{id}")
    public RequestTaskDto getTaskById(@PathVariable Long id) {
        return requestService.getTaskDtoByTaskId(id);
    }

    @PutMapping("/task/approve/{id}")
    public ResponseEntity<String> approveTask(@PathVariable Long id) {
        requestService.approveTask(id);
        return ResponseEntity.ok("Task approved");
    }

    @PutMapping("/task/reject/{id}")
    public ResponseEntity<String> rejectTask(@PathVariable Long id) {
        requestService.rejectTask(id);
        return ResponseEntity.ok("Task rejected");
    }





}
