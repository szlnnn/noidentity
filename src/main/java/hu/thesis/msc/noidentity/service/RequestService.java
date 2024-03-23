package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.dto.ClientRequestDto;
import hu.thesis.msc.noidentity.dto.RequestTaskDto;
import hu.thesis.msc.noidentity.entity.Request;
import hu.thesis.msc.noidentity.entity.RequestTask;
import hu.thesis.msc.noidentity.entity.Role;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.entity.UserRoleAssignment;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.repository.RequestRepository;
import hu.thesis.msc.noidentity.repository.RequestTaskRepository;
import hu.thesis.msc.noidentity.repository.UserAccountRepository;
import hu.thesis.msc.noidentity.repository.UserRoleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestService {

    private final UserAccountService userAccountService;

    private final UserRoleAssignmentRepository assignmentRepository;

    private final RequestRepository requestRepository;

    private final UserAccountRepository userAccountRepository;

    private final RequestTaskRepository requestTaskRepository;


    public void createRequestsFromBulkRequest(ClientRequestDto clientRequestDto) {
        ArrayList<Role> rolesToRequest = clientRequestDto.getRolesToRequest();
        ArrayList<Role> rolesToRevoke = clientRequestDto.getRolesToRevoke();
        UserAccount requester = userAccountService.getUserOrElseThrow(clientRequestDto.getRequester().getLogin());
        UserAccount targetUser = userAccountService.getUserOrElseThrow(clientRequestDto.getTargetUser().getLogin());

        rolesToRequest.forEach(role -> createRequestAndUserRoleAssignments(role, requester, targetUser, "A"));
        rolesToRevoke.forEach(role -> createRequestAndUserRoleAssignments(role, requester, targetUser, "R"));

    }

    private void createRequestAndUserRoleAssignments(Role role, UserAccount requester, UserAccount targetUser, String operation) {
        Optional<UserRoleAssignment> existingAssignment = assignmentRepository.findByRoleAndUser(role, targetUser);

        if (existingAssignment.isPresent() && "P".equals(existingAssignment.get().getAssignmentStatus())) {
            return; // ha mar van igenyles akkor valami nem okes
        }

        Request newRequest = new Request();
        newRequest.setRequester(requester);
        newRequest.setCreationTime(new Date());
        newRequest.setStatus("N");
        newRequest.setRole(role);
        newRequest.setOperation(operation);
        newRequest.setTargetUser(targetUser);

        existingAssignment.ifPresent(userRoleAssignment -> {
            userRoleAssignment.setAssignmentStatus("P");
            newRequest.setAssignment(userRoleAssignment);
        });
        if (existingAssignment.isEmpty()) {
            UserRoleAssignment newUserRoleAssignment = new UserRoleAssignment();
            newUserRoleAssignment.setRole(role);
            newUserRoleAssignment.setUser(targetUser);
            newUserRoleAssignment.setCreationTime(new Date());
            newUserRoleAssignment.setAssignmentStatus("P");
            assignmentRepository.save(newUserRoleAssignment);
            newRequest.setAssignment(newUserRoleAssignment);
        }
        requestRepository.save(newRequest);

    }


    public void processNewRequests() {
        requestRepository.findAllByStatus("N").forEach(this::handleRequests);
    }

    public void handleRequests(Request request) {
        request.setStatus("U");

        RequestTask managerTask = new RequestTask();
        UserAccount manager = userAccountService.getManagerForUser(request.getTargetUser());
        managerTask.setParentRequest(request);
        managerTask.setStatus("N");
        managerTask.setApprover(manager);
        managerTask.setCreationTime(new Date());
        managerTask.setType("manager");

        requestRepository.save(request);
        requestTaskRepository.save(managerTask);

    }


    public List<RequestTaskDto> getTasksForApprover(Long approverId) {
        Optional<UserAccount> approver =  userAccountRepository.findById(approverId);
        if (approver.isEmpty()) {
            return Collections.emptyList();
        }
        List<RequestTaskDto> responseDtos = new ArrayList<>();
        requestTaskRepository.findAllByStatusAndAndApprover("N", approver.get())
                .forEach(requestTask -> {
                    RequestTaskDto dto = convertTaskToDto(requestTask);
                    responseDtos.add(dto);
                });

        return responseDtos;

    }

    public RequestTaskDto getTaskDtoByTaskId(Long id) {
        return requestTaskRepository.findById(id)
                .map(this::convertTaskToDto)
                .orElse(null);
    }

    private RequestTaskDto convertTaskToDto(RequestTask requestTask) {
        RequestTaskDto dto = new RequestTaskDto();
        dto.setId(requestTask.getId());
        dto.setCreationTime(requestTask.getCreationTime());
        dto.setStatus(requestTask.getStatus());
        dto.setRole(requestTask.getParentRequest().getRole());
        dto.setTargetUser(requestTask.getParentRequest().getTargetUser());
        dto.setType(requestTask.getType());
        dto.setOperation(requestTask.getParentRequest().getOperation());
        return dto;
    }

    public void rejectTask(Long taskId) {
        RequestTask task = getRequestTaskForId(taskId);
        task.setCompletionTime(new Date());
        task.setStatus("R");
        requestTaskRepository.save(task);

        Request originalRequest = task.getParentRequest();
        originalRequest.setOutcome("R");
        originalRequest.setStatus("T");
        requestRepository.save(originalRequest);

        UserRoleAssignment ura = originalRequest.getAssignment();
        ura.setAssignmentStatus("R");
        ura.setRevokedTime(new Date());
        assignmentRepository.save(ura);

    }

    public void approveTask(Long taskId) {
        RequestTask task = getRequestTaskForId(taskId);

        task.setCompletionTime(new Date());
        task.setStatus("A");
        requestTaskRepository.save(task);

        if ("manager".equals(task.getType())) {
            approveManagerTask(task);
        } else if ("applicationOwner".equals(task.getType())) {
            approveAppOwnerTask(task);
        } else {
            throw  new AppException("Cannot handle task with type : " + task.getType(), HttpStatus.BAD_REQUEST);
        }
    }

    private RequestTask getRequestTaskForId(Long taskId) {
        return requestTaskRepository.findById(taskId)
                .orElseThrow(() -> new AppException("Cannot find task with id : " + taskId, HttpStatus.BAD_REQUEST));
    }

    private void approveAppOwnerTask(RequestTask task) {
        //todo
    }

    public void approveManagerTask(RequestTask task) {
        Request originalRequest = task.getParentRequest();
        RequestTask appOwnerTask = new RequestTask();
        appOwnerTask.setStatus("N");
        appOwnerTask.setCreationTime(new Date());
        appOwnerTask.setType("applicationOwner");
        appOwnerTask.setApprover(getApproverForAppOwnerTask(originalRequest));
        appOwnerTask.setParentRequest(originalRequest);
        requestTaskRepository.save(appOwnerTask);
    }

    public UserAccount getApproverForAppOwnerTask(Request request) {
        return Optional.of(request.getRole().getResource().getAppOwner())
               .orElse(userAccountService.getAdminUser());
    }

}
